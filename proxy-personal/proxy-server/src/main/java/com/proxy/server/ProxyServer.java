package com.proxy.server;

import com.proxy.common.codec.ProxyMessageDecoder;
import com.proxy.common.codec.ProxyMessageEncoder;
import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.server.dao.GatewayRedisDAO;
import com.proxy.server.dto.GatewayLogDTO;
import com.proxy.server.dto.GatewayRouteDTO;
import com.proxy.server.handler.*;
import com.proxy.server.service.*;
import com.proxy.server.task.ExitHandler;
import com.proxy.server.task.WsLauncher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyServer implements LifeCycle {

    private static Logger logger = LoggerFactory.getLogger(ProxyServer.class);


    //private final javax.net.ssl.SSLContext	sslCtx;

    /**
     * 最大帧长度 50M 当前10M
     */
    //private static final int MAX_FRAME_LENGTH = 50 * 1024 * 1024;
    private static final int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
    /**
     * 长度域偏移
     */
    private static final int LENGTH_FIELD_OFFSET = 0;
    /**
     * 长度域字节数
     */
    private static final int LENGTH_FIELD_LENGTH = 4;


    /**
     * 绑定端口,默认6666
     */
    private int port;
    /**
     * http 代理通道
     */
    private Integer httpPort;

    /**
     * 服务端channel
     */
    public Channel channel;


    public ProxyServer() {
        this.port = 6666;
        //this.sslCtx = SslContextFactory.getServerContext();
    }

    public static void main(String[] args) throws Exception {

        //加载日志
        LogBackConfigLoader.load();

        try {
            //退出钩子
            Runtime.getRuntime().addShutdownHook(new ExitHandler());
            //创建代理服务器,如果没有指定端口，则默认使用 6666 端口
            ProxyServer proxyServer = new ProxyServer();
            //将代理服务保存,方便后续使用
            ServerBeanManager.setProxyServer(proxyServer);
            //开启代理服务
            proxyServer.start();

        } catch (Exception e) {
            logger.error("启动代理服务失败：", e);
        }

        //websocket连接

    }

    //开启proxy-server 与 proxy-client的通信端口
    private ChannelFuture bind() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        SSLEngine sslEngine = sslCtx.createSSLEngine();
//                        sslEngine.setUseClientMode(false);
//                        //1.单向验证 false 双向true
//                        sslEngine.setNeedClientAuth(true);
//                        socketChannel.pipeline().addFirst(new SslHandler(sslEngine));
//                        socketChannel.pipeline().addLast(new CertParseHandler());

                        socketChannel.pipeline().addLast("idleStateHandler", new IdleStateHandler(10 * 6, 15 * 6, 20 * 6));
                        socketChannel.pipeline().addLast(new ProxyMessageDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH));
                        socketChannel.pipeline().addLast(new ProxyMessageEncoder());
                        socketChannel.pipeline().addLast(new LoginAuthRespHandler());
                        socketChannel.pipeline().addLast(new HeartBeatRespHandler());
                        socketChannel.pipeline().addLast(new ServerChannelHandler());
                    }
                });
        // 该boostrap启动与客户端的连接
        ServerBeanManager.setBootstrap(bootstrap);
        ChannelFuture future = null;
        try {
            future = bootstrap.bind(port);

            future.channel().closeFuture().addListeners((ChannelFutureListener) channelFuture -> {
                logger.info("等待代理服务退出...");

                Map<String ,Object> hostInfoMap = new HashMap<>();
                Map<String ,Object> hostInfoMap2 = new HashMap<>();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });

            logger.info("服务器监听端口 {}", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return future;
    }




    //开启转发
    private void start() {

        //读取代理服务配置文件
        ServerBeanManager.getConfigService().readServerConfig();

        ConfigService configService = ServerBeanManager.getConfigService();

        // 获取端口
        if (configService.getConfigure("port") != null) {
            this.port = (int) configService.getConfigure("port");
        }

        //启动http 转发服务
        if (configService.getConfigure("httpPort") != null) {
            this.httpPort = (int) configService.getConfigure("httpPort");
        }

        //配置代理信息
        //如果同时要启用静态配置，删掉下方注释并
        //configurProxy();

        ChannelFuture mainFuture = null;

        ChannelFuture httpFuture = null;

        try {
            //启动主程序
            mainFuture = startMainServer();

            //启动http服务(如果有配置)
            httpFuture = startHttpServer();

            //启动处理Controller请求的监听器
//            startHandlerServer();

            // 启动转发服务，暂定这样,线程池，模拟消息队列，异步转发用户的请求
            ServerBeanManager.getTransferService().start();
            //微服务模块关闭
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        WsLauncher.start();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
            mainFuture.channel().closeFuture().sync();



        } catch (Exception e) {
            if (mainFuture != null) {
                mainFuture.channel().close();
            }
            if (httpFuture != null) {
                httpFuture.channel().close();
            }
            logger.error("代理服务 启动失败：", e);
        }
        //启动ws连接控制器

    }

    private ChannelFuture startMainServer() {

        //根据配置文件启动服务
        ChannelFuture future = bind();
        this.channel = future.channel();
        return future;
    }

    /**
     * 静态配置
     * 配置代理信息 , 启动时执行，将配置信息加载到缓存中
     */
//    private void configurProxy() {
//        //获取客户端配置信息
//        Map<String, List<Map<String, Object>>> nodes = (Map<String, List<Map<String, Object>>>) ServerBeanManager.getConfigService().getConfigure("client");
//        for (Map.Entry<String, List<Map<String, Object>>> m : nodes.entrySet()) {
//            ClientNode clientNode = new ClientNode();
//            clientNode.setClientKey(m.getKey());
//            clientNode.setStatus(CommonConstant.ClientStatus.ACTIVE);
//            List<Map<String, Object>> reals = m.getValue();
//
//            //将路由信息保存到内存中
//            for (Map<String, Object> real : reals) {
//                ProxyRealServer proxy = new ProxyRealServer();
//                proxy.setClientKey(m.getKey());
//                proxy.setRealHost((String) real.get("realhost"));
//                proxy.setRealHostPort((Integer) real.get("realhostport"));
//                proxy.setDescription((String) real.get("description"));
//                String proxyType = (String) real.get("proxyType");
//                if (proxyType.equalsIgnoreCase("http")) {
//                    buildHttp(proxy, real, clientNode);
//                    continue;
//                }
//                if (proxyType.equalsIgnoreCase("tcp")) {
//                    buildTcp(proxy, real, clientNode);
//                    continue;
//                }
//                if (proxyType.equalsIgnoreCase("udp")){
//                    //buildUdp(proxy,real,clientNode);
//                    continue;
//                }
//                logger.warn("目前只支持http,tcp,不支持:{}", proxyType);
//            }
//
//            ServerBeanManager.getClientService().add(clientNode);
//        }
//
//
//
//    }


    //刷新Redis中的路由信息
    public static ClientNode addClient(GatewayRouteDTO gatewayRouteDto){
        //----------- Redis保存路由配置信息 --------------------
        ClientNode clientNode = new ClientNode();
        clientNode.setClientKey(gatewayRouteDto.getProxy_client_key());
        clientNode.setStatus(CommonConstant.ClientStatus.ACTIVE);
        GatewayRedisDAO.removeClientRoute(clientNode.getClientKey());
        List<ProxyRealServer> list =  gatewayRouteDto.getRoute();
        if(list.size()<1){
            logger.warn("client: {} 没有配置路由信息,请在管理平台添加路由信息后重新注册登录 。 ");
            return clientNode;
        }
        for(ProxyRealServer e :list) {
            clientNode.addRealServer(e.getServerPort(), e);
            if (e.getProxyType() == 1) {
                GatewayRedisDAO.addTcpRoute(e.getServerPort(), e.getAddress(),e.getClientKey());
            } else if (e.getProxyType() == 3) {
                GatewayRedisDAO.addUdpRoute(e.getServerPort(), e.getAddress(),e.getClientKey());
            }
        }

        return clientNode;
        //-------------------------------------

    }

//    /**
//     * 构建tcp 代理信息
//     *
//     * @param proxy      真实服务
//     * @param real       配置信息
//     * @param clientNode 客户端节点
//     */
//    private void buildTcp(ProxyRealServer proxy, Map<String, Object> real, ClientNode clientNode) {
//        proxy.setProxyType(CommonConstant.ProxyType.TCP);
//        proxy.setServerPort((Integer) real.get("serverport"));
//        clientNode.addRealServer(proxy.getServerPort(), proxy);
//    }
//
//    /**
//     * 构建udp代理信息
//     *
//     * @User ztx
//     * @param proxy      真实服务
//     * @param real       配置信息
//     * @param clientNode 客户端节点
//     */
//    private void buildUdp(ProxyRealServer proxy,Map<String,Object> real,ClientNode clientNode){
//        proxy.setProxyType(CommonConstant.ProxyType.UDP);
//        proxy.setServerPort((Integer) real.get("serverport"));
//        clientNode.addRealServer(proxy.getServerPort(), proxy);
//    }
//
//
//    /**
//     * 构建 http 代理信息
//     *
//     * @param proxy      真实服务
//     * @param real       配置信息
//     * @param clientNode 客户端节点
//     */
//    private void buildHttp(ProxyRealServer proxy, Map<String, Object> real, ClientNode clientNode) {
//
//        proxy.setDomain((String) real.get("domain"));
//        Integer serverport = (Integer) real.get("serverport");
//        String domain = proxy.getDomain();
//
//        if (domain != null && this.httpPort == null) {
//            logger.error("配置文件出错,http域名代理需要配置httpPort端口");
//            throw new RuntimeException();
//        }
//
//        if (StringUtils.isBlank(domain) && serverport == null) {
//            logger.error("配置文件出错,http代理至少要有serverport或者domain一种");
//            throw new RuntimeException();
//        }
//        proxy.setServerPort(serverport);
//        proxy.setProxyType(CommonConstant.ProxyType.HTTP);
//        clientNode.addRealServer(proxy.getDomain() == null ? serverport : proxy.getDomain(), proxy);
//    }

    private ChannelFuture startHttpServer() throws RuntimeException {

        if (httpPort == null) {
            return null;
        }

        //启动http 转发服务

        //绑定客户端服务端口
        NioEventLoopGroup serverWorkerGroup = new NioEventLoopGroup();
        NioEventLoopGroup serverBossGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        //暂时先关闭
                        //ch.pipeline().addLast(SharableHandlerManager.getTrafficLimitHandler());
                        //ch.pipeline().addLast(SharableHandlerManager.getTrafficCollectionHandler());
                        //http请求消息解码器
                        ch.pipeline().addLast("httpDecoder", new HttpRequestDecoder());

                        ch.pipeline().addLast("connectHandler", new HttpNoticeChannelHandler());
                        //解析 HTTP POST 请求
                        ch.pipeline().addLast("httpObject", new HttpObjectAggregator(2 * 1024 * 1024));
                        ch.pipeline().addLast("transferHandler", new HttpChannelHandler());
                    }
                });
        try {
            //绑定服务端口,会更新代理状态
            ChannelFuture future = ServerBeanManager.getProxyChannelService().bind(this.httpPort, bootstrap, CommonConstant.ProxyType.HTTP, this.httpPort);
            future.channel().closeFuture().addListeners((ChannelFutureListener) channelFuture -> {
                logger.info("等待http代理服务退出...");
                serverWorkerGroup.shutdownGracefully();
                serverBossGroup.shutdownGracefully();
            });
            return future;
        } catch (Exception e) {
            logger.error("http服务端口 {} 绑定失败:" + e.getMessage(), this.httpPort);
            throw new RuntimeException();
        }
    }

    @Override
    public void shutDown() {
        try {
            this.channel.close();
            logger.debug("{}端口:代理服务退出:", this.port);
        } catch (Exception e) {
            logger.error("代理服务退出异常:", e);
        }
    }

    //处理 "控制器" 发过来的HTTP请求
//    private void startHandlerServer() throws RuntimeException, InterruptedException {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NioEventLoopGroup serverWorkerGroup = new NioEventLoopGroup();
//                NioEventLoopGroup serverBossGroup = new NioEventLoopGroup();
//                ServerBootstrap bootstrap = new ServerBootstrap();
//                bootstrap.group(serverBossGroup, serverWorkerGroup)
//                        .channel(NioServerSocketChannel.class)
//                        .childHandler(new ChannelInitializer<SocketChannel>() {
//                            @Override
//                            public void initChannel(SocketChannel ch) {
//                                //http请求消息解码器
//                                ch.pipeline().addLast("httpCodec", new HttpServerCodec());
//                                //解析 HTTP POST 请求
//                                ch.pipeline().addLast("httpAggregator", new HttpObjectAggregator(2 * 1024 * 1024));
//                                ch.pipeline().addLast("HttpRequestHandler", new HttpRequestHandler());
//                            }
//                        });
//
//                try {
//                    int serverPort = (Integer) ServerBeanManager.getConfigService().getConfigure("servicePort");
//                    ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(serverPort)).sync();
//                    channelFuture.sync();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                logger.info("控制器连通器已开启");
//            }
//        }).start();
//
//    }
}
