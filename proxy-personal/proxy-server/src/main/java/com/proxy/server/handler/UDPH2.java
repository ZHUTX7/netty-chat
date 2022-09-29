package com.proxy.server.handler;

import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.entity.server.ProxyChannel;
import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.util.ProtostuffUtil;
import com.proxy.common.util.ProxyMessageUtil;
import com.proxy.server.service.ProxyManager;
import com.proxy.server.service.ServerBeanManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author ztx
 * @date 2021-07-20 10:29
 * @description :  UDP 接收器
 * TODO (待研究)  该类是否继承 SimpleChannelInboundHandler  ,SimpleChannelInboundHandler在读取数据后自动release ，
 *     但是在本项目的业务逻辑中，该数据需要转发， 转发之前可能先进消息队列， 那读取后直接 release 是否会出现问题
 */
public class UDPH2 extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Logger logger = LoggerFactory.getLogger(UDPH2.class);

    public UDPH2() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        logger.info(
                "收到客户端: "+datagramPacket.sender().getAddress()+":"+ datagramPacket.sender().getPort()+"发来的UDP包");

        //获取发送的端口
        //datagramPacket.sender().getPort()
        Channel userChannel = channelHandlerContext.channel();
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        //前置条件， 代理客户已经注册，且代理信息已经添加到缓存类中
        ProxyChannel proxyChannel = ServerBeanManager.getProxyChannelService().getServerProxy(sa.getPort());
        //设置代理客户端的channel
        ClientNode node = ServerBeanManager.getClientService().get(proxyChannel.getClientKey());

        ProxyRealServer proxyRealServer = node.getServerPort2RealServer().get(sa.getPort());
        System.out.println(" proxyRealServer - address"+proxyRealServer.getAddress());
        System.out.println(" proxyRealServer - port"+proxyRealServer.getServerPort());

        if (node == null || node.getChannel() == null || node.getStatus() != CommonConstant.ClientStatus.ONLINE) {
            logger.error("端口{} 没有UDP代理客户端", sa.getPort());
            return;
        }
        ByteBuf buf = datagramPacket.content().copy();
        // 1.获取转发地址
        String targetHost = node.getServerPort2RealServer().get(sa.getPort()).getRealHost();
        int targetPort = node.getServerPort2RealServer().get(sa.getPort()).getRealHostPort();
        // 2.构建转发包，封装到TCP中

        DatagramPacket transferPacket =new DatagramPacket(buf,
                new InetSocketAddress(targetHost,targetPort));
        // 3.将transferPacket 序列化并构建proxyMessage
        if(transferPacket==null){
            System.out.println("null-----");
            return;
        }
       // byte[] bytes = ProtostuffUtil.serialize(transferPacket);
        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setData("hello".getBytes(StandardCharsets.UTF_8));
        proxyMessage.setProxyType((byte)3);
        proxyMessage.setType(CommonConstant.MessageType.TYPE_TRANSFER);
//        ProxyMessage proxyMessage = ProxyMessageUtil.buildMsg(null, CommonConstant.MessageType.TYPE_TRANSFER,
//                (byte)CommonConstant.ProxyType.UDP, null, null,bytes );
        // 4.发送
        node.getChannel().writeAndFlush(proxyMessage);
    }



}

