package com.proxy.server.handler;


import com.proxy.common.entity.server.ClientNode;
import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.util.ProxyMessageUtil;
import com.proxy.server.dao.ClientKeyTokenDao;
import com.proxy.server.service.PortManager;
import com.proxy.server.service.ServerBeanManager;
import com.proxy.server.util.RedisPoolDao;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * 心跳处理 handler
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    private int heartbeatCount = 0;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ProxyMessage) {

            ProxyMessage message = (ProxyMessage) msg;
            byte type = message.getType();
            //如果是心跳请求消息
            if (type == CommonConstant.HearBeat.TYPE_HEARTBEAT_REQ) {
                logger.info("收到客户端({})心跳消息",ctx.channel().remoteAddress());
                //构建心跳响应消息
                ProxyMessage heartBeat = ProxyMessageUtil.buildHeartBeatResp();
                ctx.writeAndFlush(heartBeat);
            } else {
                //向上传递消息
                ctx.fireChannelRead(msg);
            }
        } else {
            //向后转发消息
            ctx.fireChannelRead(msg);

            //消息格式错误,关闭用户连接
            logger.info("客户端消息格式错误,无法解码");
            //关闭用户连接
            ctx.channel().close();
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        disConnectHandle(ctx.channel());

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;

            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    /**
     * 和客户端通信超时处理(这里超时:n倍心跳时间)
     *
     * @param ctx
     */
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        disConnectHandle(ctx.channel());
        logger.debug("READER_IDLE 读超时");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        disConnectHandle(ctx.channel());
        logger.debug("WRITER_IDLE 写超时");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        disConnectHandle(ctx.channel());
        logger.debug("ALL_IDLE 写超时");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("发生异常:{}", cause.getMessage()+"/n"+cause.toString()+"/n"+cause.getStackTrace());
        cause.printStackTrace();
        disConnectHandle(ctx.channel());
    }


    /**
     * 代理服务器和客户端失去连接
     *
     * @param channel
     */
    private void disConnectHandle(Channel channel) {
        // 获取客户端key
        String key = ServerBeanManager.getClientService().getClientKey(channel);

        if (key==null){
            channel.close();
            return;
        }

        //获取客户端节点信息
        ClientNode node = ServerBeanManager.getClientService().get(key);
        if(node == null){
            //已经移除
            logger.info("已经移除客户端");
            return;
        }
        //更新节点 状态(离线)
        ServerBeanManager.getClientService().setNodeStatus(key, CommonConstant.ClientStatus.OFFLINE);

        //移除客户端 channel
        ServerBeanManager.getClientService().setNodeChannle(key, null);

        // TODO 修改此处2021/9/2
        //取消代理通道
        Map<Object, ProxyRealServer> port2RealServers = node.getServerPort2RealServer();
        int serverID  =  (int)ServerBeanManager.getConfigService().getConfigure("id");

        Jedis jedis = RedisPoolDao.getRedisPool().getResource();
        for (Map.Entry<Object, ProxyRealServer> entry : port2RealServers.entrySet()) {

            if (entry.getKey() instanceof Integer) {
                //释放绑定的代理服务器 服务端口
                ServerBeanManager.getProxyChannelService().unBind((Integer) entry.getKey());
                //ServerBeanManager.getProxyChannelService().getServerProxy(entry.getKey()).setStatus(CommonConstant.ProxyStatus.OFFLINE);
                logger.info("解绑本地服务端口({})成功 客户端({})--{}", entry.getKey(), entry.getValue().getClientKey(), entry.getValue().getDescription());
                //移除端口信息，方便下次自动分配
                PortManager.getUsedPortList().remove(entry.getKey());

                String address = entry.getValue().getAddress();
                try{
                    jedis.srem(address,serverID+"");
                }catch (Exception e){
                    logger.error("删除路由过滤表失败：address({}),网关ID({})",address,serverID);
                }
            }
        }
        jedis.close();


        //Reid这边删除配置
        //String token = ClientKeyTokenDao.getTokenByClientKey(node.getClientKey());
        //客户端下线,网关移除配置信息
        logger.info("代理服务器与客户端({})失去连接", node.getClientKey());
        logger.info("redis移除配置信息 !");
        //关闭连接
        channel.close();
        ServerBeanManager.getClientService().delete(key);


    }
}
