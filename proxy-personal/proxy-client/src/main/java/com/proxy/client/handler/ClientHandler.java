package com.proxy.client.handler;

import com.proxy.client.dao.RedisPoolDao;
import com.proxy.client.service.ClientBeanManager;

import com.proxy.common.entity.client.RealServer;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.protocol.RedisKeyNameConfig;
import com.proxy.common.util.ProtostuffUtil;
import com.proxy.common.util.ProxyMessageUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;



public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private JedisPool jedis = RedisPoolDao.getRedisPool();

    /**
     * 用于连接真实服务器
     */
    private Bootstrap realServerBootStrap;

    public ClientHandler(Bootstrap realServerBootStrap) {
        this.realServerBootStrap = realServerBootStrap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ProxyMessage message = (ProxyMessage) msg;
        byte type = message.getType();
        switch (type) {
            case CommonConstant.MessageType.TYPE_TRANSFER:
                handleTransferMessage(ctx, message);
                break;
            //收到来自proxy-server的通知信息，通知client与真实服务器建立连接
            case CommonConstant.MessageType.TYPE_CONNECT_REALSERVER:
                handleConnectMessage(ctx, message);
                break;

            //代理客户端与真实服务器连接断开
            case CommonConstant.MessageType.TYPE_DISCONNECT:
                handleDisConnectMessage(ctx, message);
                break;

            default:
                break;
        }
    }

    private void handleTransferMessage(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        if(proxyMessage.getProxyType() == CommonConstant.ProxyType.UDP){
            // 如果是UDP消息， 同样也上抛
            System.out.println("收到UDP");
            ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
            buf.writeBytes(proxyMessage.getData());
            DatagramPacket datagramPacket  = ProtostuffUtil.deserialize(proxyMessage.getData(),DatagramPacket.class);
            ctx.channel().writeAndFlush(datagramPacket);
        }
        Long sessionID = proxyMessage.getSessionID();

        RealServer realServer = ClientBeanManager.getProxyService().getRealServerChannel(sessionID);
        Channel realServerChannel;

//        //TODO : FIXED: Redis - 排行榜 - Host被访问次数   以下可能导致被阻塞，原因待研究 ----------------------
         //解决方案：后端线程池 .还是遇到线程池
         //Log2Redis.executeTask(realServer.getRealHost());
        // ----------------------------------------------------------------------------
        if ((realServer != null) && (realServerChannel = realServer.getChannel()) != null) {

            byte requestType = proxyMessage.getProxyType();
            if (requestType == CommonConstant.ProxyType.TCP) {

                //1.如果消息是tcp类型
                //TODO  proxy-client需要解密流量
                ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
                buf.writeBytes(proxyMessage.getData());
                realServerChannel.writeAndFlush(buf);
                logger.debug("客户端转发tcp请求至真实服务器");

            } else if (requestType == CommonConstant.ProxyType.HTTP) {

                //2.如果消息是http类型
                ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
                buf.writeBytes(proxyMessage.getData());
                ctx.fireChannelRead(buf);
            }

        } else {
            logger.debug("代理客户端未连接真实服务器,需要重新发起连接请求");

            // TODO: 需要fix

            //方案1:通知连接
            //目前:检测到和真实服务器失去连接后，会通知代理服务器断开与用服务器关闭用户的请求,让用户重新发起新的户的连接
            proxyMessage = ProxyMessageUtil.buildReConnect(sessionID, null);
            ctx.channel().writeAndFlush(proxyMessage);

            //方案2:缓存数据,客户端重新与真实服务器建立连接,当连接建立成功后,再转发数据

        }
    }

    private void handleConnectMessage(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {

        final Channel channel = ctx.channel();

        //会话id
        final Long sessionID = proxyMessage.getSessionID();
        //代理类型
        int proxyType = proxyMessage.getProxyType() & 0xFF;

        //代理服务器地址,用于重定向的时候替换header 中的Location地址
        final String proxyServer = new String(proxyMessage.getCommand());

        //真实服务器地址：ip:port
        String[] serverInfo = new String(proxyMessage.getData()).split(":");

        //真实服务器ip
        final String ip = serverInfo[0];

        //真实服务器端口
        final int port = Integer.parseInt(serverInfo[1]);


        realServerBootStrap.connect(ip, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {

                //保存 proxy-client与真实服务器的连接
                Channel realServerChannel = future.channel();
                logger.debug("客户端连接真实服务器成功{}", ip + ":" + port);
                RealServer realServer = new RealServer();

                realServer.setChannel(realServerChannel);
                realServer.setRealHost(ip);
                realServer.setRealHostPort(port);
                realServer.setProxyType(proxyType);
                realServer.setStatus(CommonConstant.ProxyStatus.ONLINE);

                ClientBeanManager.getProxyService().addRealServerChannel(sessionID, realServer, realServerChannel
                        , String.valueOf(proxyType), proxyServer);


                ProxyMessage proxyMessage1 = ProxyMessageUtil.buildConnectSuccess(sessionID, null);
                //通知proxy-server ， proxy-client已经与真实服务器建立请求
                channel.writeAndFlush(proxyMessage1);
            } else {
                logger.error("客户端连接真实服务器({})失败:{}", ip + ":" + port, future.cause().getMessage());
                ProxyMessage proxyMessage1 = ProxyMessageUtil.buildConnectFail(sessionID, null);
                channel.writeAndFlush(proxyMessage1);
            }
        });
    }

    private void handleDisConnectMessage(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {

        Long sessionID = proxyMessage.getSessionID();

        String[] serverInfo = new String(proxyMessage.getData()).split(":");
        final String ip = serverInfo[0];
        final int port = Integer.parseInt(serverInfo[1]);

        RealServer realServer = ClientBeanManager.getProxyService().getRealServerChannel(sessionID);
        Channel realServerChannel;
        if (realServer != null && (realServerChannel = realServer.getChannel()) != null) {
            realServerChannel.close();
            ClientBeanManager.getProxyService().removeRealServerChannel(sessionID);
            logger.debug("客户端与真实服务器{}断开", ip + ":" + port);
        }

    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {

        logger.debug("客户端和代理服务器连接通道可写");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        ClientBeanManager.getProxyService().clear();
        ctx.channel().close();
        logger.error("发生异常:清理数据,客户端退出" + cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ClientBeanManager.getProxyService().clear();
        ctx.channel().close();
        logger.info("和服务器连接断开:清理数据");
    }
}
