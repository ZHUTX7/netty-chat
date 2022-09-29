package com.proxy.client;

import com.proxy.client.handler.*;
import com.proxy.client.service.ClientBeanManager;
import com.proxy.client.service.LauncherSwitchService;
import com.proxy.client.service.LogBackConfigLoader;
import com.proxy.common.codec.ProxyMessageDecoder;
import com.proxy.common.codec.ProxyMessageEncoder;
import com.proxy.common.codec.http.MyHttpObjectAggregator;
import com.proxy.common.codec.http.MyHttpRequestDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;


public class ProxyClient {

    private static Logger logger = LoggerFactory.getLogger(ProxyClient.class);


  // private final javax.net.ssl.SSLContext	sslCtx = SslContextFactory.getClientContext();
    /**
     * 服务器地址,默认127.0.0.1
     */
    private String host;


    /**
     * 服务器端口,默认6666
     */
    private int port;

    /**
     * 10M
     */
    private int maxContentLength = 10 * 1024 * 1024 ;


    /**
     * 客户端启动器
     */
    private Bootstrap clientBootstrap;


    /**
     * 连接真实服务器启动器,使用时才初始化
     */
    private static Bootstrap realServerBootstrap;

    /**
     * NioEventLoopGroup可以理解为一个线程池,
     * 内部维护了一组线程，每个线程负责处理多个Channel上的事件,
     * 而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题。
     */
    private NioEventLoopGroup clientGroup;

    /**
     * 用于真实服务器,使用时 才初始化
     */
    private NioEventLoopGroup realServerGroup;

    /**
     * 用于向真实服务器转发udp请求
     */
    private Bootstrap udpBootstrap;



    public ProxyClient(String host, int port) {
        // 消除该注释即开启TLS   1/2
        //sslCtx = SslContextFactory.getClientContext();
        //sslCtx =null;
        this.host = host;
        this.port = port;
        clientBootstrap = new Bootstrap();
        clientGroup = new NioEventLoopGroup();
    }


    /**
     * 连接代理服务器
     */
    public void start() throws InterruptedException {

        initRealServerBoot();

        clientBootstrap.group(clientGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    //初始化时将handler设置到ChannelPipeline
                    @Override
                    public void initChannel(SocketChannel ch) {
// 消除该注释即开启TLS   2/2
//                        SSLEngine sslEngine = sslCtx.createSSLEngine();
//                        sslEngine.setUseClientMode(true);
//                        sslEngine.setNeedClientAuth(true);
//                        ch.pipeline().addFirst(new SslHandler(sslEngine));
//                        ch.pipeline().addLast(new CertParseHandler());


                        //ch.pipeline().addLast("logs", new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(10 * 3, 15 * 3, 20 * 3));
                        ch.pipeline().addLast(new ProxyMessageDecoder(maxContentLength, 0, 4));
                        ch.pipeline().addLast(new ProxyMessageEncoder());
                        ch.pipeline().addLast(new LoginAuthReqHandler());
                        ch.pipeline().addLast(new HeartBeatReqHandler(getInstance()));
                        ch.pipeline().addLast(new ClientHandler(realServerBootstrap));
                        ch.pipeline().addLast(new MyHttpRequestDecoder());
                        ch.pipeline().addLast(new MyHttpObjectAggregator(maxContentLength));
                        ch.pipeline().addLast(new HttpReceiveHandler());
                    }
                });

        /**
         * 最多尝试5次和服务端连接(总计数,不是连续尝试次数)
         */
       doConnect();

//        try {
//            clear();
//        } catch (Exception ignored) {
//
//        }

    }

    /**
     * 初始化 连接后端真正服务器
     */
    private void initRealServerBoot() throws InterruptedException {
        int udpProxyPort =Integer.parseInt(ClientBeanManager.getConfigService().readConfig().get("udpProxyPort")) ;
        //初始化
        realServerBootstrap = new Bootstrap();
        realServerGroup = new NioEventLoopGroup();
        realServerBootstrap.group(realServerGroup);
        realServerBootstrap.channel(NioSocketChannel.class);
        realServerBootstrap.option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        realServerBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TCPHandler());
                ch.pipeline().addLast(new HttpResponseDecoder());
                ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
                ch.pipeline().addLast(new HttpSendHandler());
            }
        });

        udpBootstrap = new Bootstrap();
        NioEventLoopGroup udpGroup = new NioEventLoopGroup();
        udpBootstrap.group(udpGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)  //广播
                .option(ChannelOption.SO_RCVBUF, 2 * 2048 * 1024)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//
                        pipeline.addLast(new UDPHandler());

                    }
                }).bind(udpProxyPort).sync();
        logger.info("UDP接收器启动成功,接收端口号为: "+udpProxyPort);
    }

    public void doConnect()  {
        ChannelFuture future  = clientBootstrap.connect(host, port).addListener((ChannelFutureListener) channelFuture ->
        {
            if (channelFuture.isSuccess()) {
                logger.info("成功连接到服务器："+channelFuture.channel().remoteAddress());
            } else {
                logger.warn("连接服务器({}:{})失败，5秒后尝试重新连接...：",host, port);
                channelFuture.channel().eventLoop().schedule(() -> {
                    doConnect();
                }, 5, TimeUnit.SECONDS);
            }
        });



    }

    private void clear() {
        ClientBeanManager.getProxyService().clear();
        clientGroup.shutdownGracefully();
        realServerGroup.shutdownGracefully();
    }


    private ProxyClient getInstance(){
        if(this == null){
            return null;
        }
        else
            return this;
    }

    public static void main(String[] args) throws Exception {
        //初始化
        LogBackConfigLoader.load();
        //websocket 注册
        //WebSocketClient.start();
        //启动netty代理
        LauncherSwitchService.start(args);
    }}
