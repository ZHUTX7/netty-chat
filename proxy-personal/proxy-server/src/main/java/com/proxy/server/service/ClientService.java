package com.proxy.server.service;

import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.server.dao.ClientDao;
import com.proxy.server.handler.*;
import com.proxy.server.util.RedisPoolDao;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理客户端 管理
 * proxy-server 开启端口，执行路由代理
 */
public class ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientService.class);

    private static ClientDao clientDao = new ClientDao();

    public  Map<String,Integer> add(ClientNode node) {

        Map<String,Integer> map = new HashMap<>();
        //1.添加客户端
        clientDao.add(node.getClientKey(), node);
        if (node.getStatus() != CommonConstant.ClientStatus.ONLINE) {
            return null;
        }
        //2.开始为客户端绑定服务端口

        //绑定客户端服务端口
        Map<Object, ProxyRealServer> keyToNode = node.getServerPort2RealServer();
        for (Map.Entry<Object, ProxyRealServer> keyToProxyRealServer : keyToNode.entrySet()) {
            int proxyPort = -1;
            ProxyRealServer proxyRealServer = keyToProxyRealServer.getValue();
            /**
             * 如果是HTTP代理,并且设置了通过域名访问,则不需要单独绑定端口
             */
            if (proxyRealServer.getProxyType() == CommonConstant.ProxyType.HTTP && StringUtils.isNotBlank(proxyRealServer.getDomain())) {
                ServerBeanManager.getProxyChannelService().addByServerdomain(proxyRealServer.getDomain(), proxyRealServer);
                proxyRealServer.setStatus(CommonConstant.ProxyStatus.ONLINE);
                continue;
            }

            if (proxyRealServer.getProxyType() == CommonConstant.ProxyType.HTTP) {

                //http 端口代理绑定
                proxyPort =  HttpProxy(keyToProxyRealServer.getKey(), proxyRealServer);

            } else if (proxyRealServer.getProxyType() == CommonConstant.ProxyType.TCP) {
                //tcp 端口代理绑定
                proxyPort = TCPProxy(keyToProxyRealServer.getKey(), proxyRealServer);
            } else if (proxyRealServer.getProxyType()== CommonConstant.ProxyType.UDP){
                proxyPort =  UDPProxy(keyToProxyRealServer.getKey(), proxyRealServer);
            }
            map.put(proxyRealServer.getId()+"",proxyPort);
        }
        return map;
    }

    /**
     * 绑定tcp 代理
     *  return  proxy-server  代理端口
     * @param key             端口
     * @param proxyRealServer 真正的服务
     */
    public int TCPProxy(Object key, ProxyRealServer proxyRealServer) {
        int gatewayID = (int)ServerBeanManager.getConfigService().getConfigure("id");
        NioEventLoopGroup serverWorkerGroup;
        NioEventLoopGroup serverBossGroup;
        int serverPort = 0;
        if (key instanceof Integer) {
            serverPort = (int) key;
            if (ServerBeanManager.getProxyChannelService().getServerProxy(serverPort) != null && ServerBeanManager.getProxyChannelService().getServerProxy(serverPort).getStatus() == CommonConstant.ProxyStatus.ONLINE) {
                logger.error("服务端口 {} 已经被绑定了，尝试动态分配...", key);
            }
            if(serverPort==-1){
                //需要此时分配 TODO:333
                serverPort =  PortManager.distributePort();
            }
            serverBossGroup = new NioEventLoopGroup();
            serverWorkerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            //  限流
                           // ch.pipeline().addLast(SharableHandlerManager.getTrafficLimitHandler());
                            //  统计流量
                            ch.pipeline().addLast(SharableHandlerManager.getTrafficCollectionHandler());
                            ch.pipeline().addLast(new TCPChannelHandler());
                        }
                    });
            try {
                //绑定服务端口,会更新代理状态
                ServerBeanManager.getProxyChannelService().bindForTCP(serverPort, bootstrap, proxyRealServer);
            } catch (Exception e) {
                serverPort = -1;
                logger.error("服务端口 {} 绑定失败:" + e.getMessage(), key);
            }

        }
        return serverPort;
    }


    /**
     * 绑定UDP 代理
     *
     * @param key             端口
     * @param proxyRealServer 真正的服务
     */
    public int UDPProxy(Object key, ProxyRealServer proxyRealServer) {
        int gatewayID = (int)ServerBeanManager.getConfigService().getConfigure("id");
        EventLoopGroup group;
        int serverPort = 0;
        if (key instanceof Integer) {
            serverPort = (int) key;
            if (ServerBeanManager.getProxyChannelService().getServerProxy(serverPort) != null && ServerBeanManager.getProxyChannelService().getServerProxy(serverPort).getStatus() == CommonConstant.ProxyStatus.ONLINE) {
                logger.error("服务端口 {} 已经被绑定了", key);
            }
            if(serverPort==-1){
                //需要此时分配 TODO:333
                serverPort =  PortManager.distributePort();
            }
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, false)  //广播
                    .option(ChannelOption.SO_RCVBUF, 2 * 2048 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {

                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast( new UDPChannelHandler());
                        }
                    });

            try {
                //绑定服务端口,会更新代理状态
                ServerBeanManager.getProxyChannelService().bindForUDP(serverPort, bootstrap, proxyRealServer);
            } catch (Exception e) {
                logger.error("服务端口 {} 绑定失败:" + e.getMessage(), key);
                serverPort = -1;
            }
        }
        else{
            System.out.println("key is not integer");

        }
        return serverPort;
    }


    /**
     * 绑定http代理
     *
     * @param key             端口
     * @param proxyRealServer 真正的服务
     */
    public int HttpProxy(Object key, ProxyRealServer proxyRealServer) {
        String gatewayID = (String)ServerBeanManager.getConfigService().getConfigure("id");

        NioEventLoopGroup serverWorkerGroup;
        NioEventLoopGroup serverBossGroup;

        int serverPort = 0;
        if (key instanceof Integer) {
            serverPort = (int) key;
            if (ServerBeanManager.getProxyChannelService().getServerProxy(serverPort) != null && ServerBeanManager.getProxyChannelService().getServerProxy(serverPort).getStatus() == CommonConstant.ProxyStatus.ONLINE) {
                logger.error("服务端口 {} 已经被绑定了", key);
            }
            if(serverPort==-1){
                //需要此时分配 TODO:333
                serverPort =  PortManager.distributePort();
            }
            serverBossGroup = new NioEventLoopGroup();
            serverWorkerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(SharableHandlerManager.getTrafficLimitHandler());
                            ch.pipeline().addLast(SharableHandlerManager.getTrafficCollectionHandler());
                            //HttpRequestDecoder http请求消息解码器
                            ch.pipeline().addLast("httpDecoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("connectHandler", new HttpNoticeChannelHandler());
                            //解析 HTTP POST 请求
                            ch.pipeline().addLast("httpObject", new HttpObjectAggregator(2 * 1024 * 1024));
                            ch.pipeline().addLast("transferHandler", new HttpChannelHandler());

                        }
                    });
            try {
                //绑定服务端口,会更新代理状态
                ServerBeanManager.getProxyChannelService().bindForTCP(serverPort, bootstrap, proxyRealServer);

            } catch (Exception e) {
                logger.error("服务端口 {} 绑定失败:" + e.getMessage(), key);
                serverPort = -1;
            }
        }
        return serverPort;
    }


    public String getClientKey(Channel channel) {
        return clientDao.getClientKey(channel);
    }

    public void delete(String clientKey) {
        if (clientKey != null)
            clientDao.remove(clientKey);
    }

    public void setNodeStatus(String clientKey, Integer status) {
        clientDao.setNodeStatus(clientKey, status);
    }

    public ClientNode get(String clientKey) {
        if (clientKey != null)
            return clientDao.get(clientKey);
        return null;
    }

    public void setNodeChannle(String clientKey, Channel channel) {
        clientDao.setNodeChannle(clientKey, channel);
    }

    public Map<String, ClientNode> getAllNode() {
        return clientDao.getAll();
    }


}
