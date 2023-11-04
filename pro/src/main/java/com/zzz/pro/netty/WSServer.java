package com.zzz.pro.netty;
//
//import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
//import com.alibaba.nacos.api.naming.NamingFactory;
//import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;

/**
 * @author ztx
 * @date 2021-12-15 16:00
 * @description : 主通信处理程序
 */
@Component
public class WSServer {

    private static Logger logger = LoggerFactory.getLogger(WSServer.class);
    //单例静态
    private static class SingletonWSServer {
        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {return SingletonWSServer.instance;}
    //处理线程池
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    //启动器
    private ServerBootstrap bootstrap;
    private ChannelFuture future;

    public WSServer(){
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss,worker)
                .option(ChannelOption.SO_BACKLOG,1024)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());

    }

    public void start(Integer port) throws InterruptedException {

        this.future = bootstrap.bind(port);
        logger.info("通信主程序启动成功，端口号："+port);
    }

}
