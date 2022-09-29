//package com.proxy.client.service;
//
//import com.proxy.client.ProxyClient;
//import com.proxy.client.handler.UDPHandler;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoop;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioDatagramChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
////  TODO： udp转发器 - 3/22
//public class UdpTransServer {
//    private static Logger logger = LoggerFactory.getLogger(UdpTransServer.class);
//
////    public void start(int port) throws InterruptedException {
////        initialServer(port).;
////    }
//
//    private Bootstrap initialServer(int port) throws InterruptedException {
//        NioEventLoopGroup udpGroup = new NioEventLoopGroup();
//        Bootstrap udpBootstrap = new Bootstrap();
//        udpBootstrap.group(udpGroup)
//                .channel(NioDatagramChannel.class)
//                .option(ChannelOption.SO_BROADCAST, true)  //广播
//                .option(ChannelOption.SO_RCVBUF, 2 * 2048 * 1024)
//                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
//                .handler(new ChannelInitializer<NioDatagramChannel>() {
//
//                    @Override
//                    protected void initChannel(NioDatagramChannel ch) throws Exception {
//                        ChannelPipeline pipeline = ch.pipeline();
////
//                        pipeline.addLast(new UDPHandler());
//
//                    }
//                }).bind(port).sync();
//        logger.info("UDP接收器启动成功,接收端口号为: "+udpProxyPort);
//        return udpBootstrap;
//    }
//}
