package com.proxy.client.handler;

import com.proxy.client.service.ClientBeanManager;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.util.ProxyMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TCPHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(TCPHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        Channel realServerChannel = ctx.channel();


        int proxyType = ClientBeanManager.getProxyService().getProxyType(realServerChannel);
        //获取proxy-client和proxy-server之间的通道
        Channel channel = ClientBeanManager.getProxyService().getChannel();

        if (channel == null) {
            // 代理客户端连接断开
            logger.debug("客户端和代理服务器失去连接");
            ctx.channel().close();
            ReferenceCountUtil.release(msg);
        } else {

            //http 消息
            if (proxyType == CommonConstant.ProxyType.HTTP) {
                //向上传递
                ctx.fireChannelRead(msg);
            } else {
                logger.debug("转发TCP消息到代理服务器（Proxy-Server) ");
                ByteBuf buf = (ByteBuf) msg;
                //TODO 此处 DATA中的数据应该是经过Android中根密钥加密的，proxy-server不做解密，proxy-client解密并转发到后端
                byte[] data = new byte[buf.readableBytes()];
                buf.readBytes(data);
                buf.release();
                Long sessionID = ClientBeanManager.getProxyService().getRealServerChannelSessionID(realServerChannel);
                ProxyMessage proxyMessage = ProxyMessageUtil.buildMsg(sessionID, CommonConstant.MessageType.TYPE_TRANSFER, null, null, null, data);
                channel.writeAndFlush(proxyMessage);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.debug("异常:与真实服务器连接断开:" + cause.getMessage());
        removeConnect(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("与真实服务器连接断开");
        removeConnect(ctx);
    }

    private void removeConnect(ChannelHandlerContext ctx) {
        Long sessionID = ClientBeanManager.getProxyService().getRealServerChannelSessionID(ctx.channel());
        ClientBeanManager.getProxyService().removeRealServerChannel(sessionID);

        //
        ProxyMessage proxyMessage = ProxyMessageUtil.buildReConnect(sessionID, null);
        Channel channel = ClientBeanManager.getProxyService().getChannel();
        channel.writeAndFlush(proxyMessage);
    }
}
