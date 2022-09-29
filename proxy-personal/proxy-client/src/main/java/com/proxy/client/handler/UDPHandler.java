package com.proxy.client.handler;
import com.proxy.client.dao.RedisPoolDao;
import com.proxy.client.service.ClientBeanManager;
import com.proxy.client.service.ConfigService;
import com.proxy.common.protocol.RedisKeyNameConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author ztx
 * @date 2021-07-21 14:33
 * @description :UDP包处理类
 */
public class UDPHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Logger logger = LoggerFactory.getLogger(UDPHandler.class);
    private static String clientKey = (String)ClientBeanManager.getConfigService().readConfig().get("proxyClientKey");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {

        datagramPacket.retain();
//        //写入到代理客户端
        ByteBuf co = datagramPacket.content();

        JedisPool jedis = RedisPoolDao.getRedisPool();
        String address = jedis.getResource().hget(RedisKeyNameConfig.getUdpRout(clientKey), datagramPacket.sender().getPort()+"");
        if(address==null||"".equals(address)){
            datagramPacket.release();
            ReferenceCountUtil.release(co);
            return;
        }
        String[] strs =  StringUtils.split(address,":");
        //区分类型 TODO :回送测试
        String serverAddress = datagramPacket.sender().getAddress().toString();
        // TYPE 1： 转发给后台应用服务器
        if(serverAddress.contains("127.0.0.1")){
            logger.info(
                    "收到proxy-server: "+datagramPacket.sender().getAddress()+":"+ datagramPacket.sender().getPort()+"发来的UDP包");

            ctx.channel().writeAndFlush(new DatagramPacket(co,
                    new InetSocketAddress(strs[0],Integer.parseInt(strs[1])),datagramPacket.sender()));
            datagramPacket.release();
            ReferenceCountUtil.release(co);
        }else{
            logger.info(
                    "收到服务器处理后UDP包: "+datagramPacket.sender().getAddress()+":"+ datagramPacket.sender().getPort()+"发来的UDP包");

            ctx.channel().writeAndFlush(new DatagramPacket(co,
                    new InetSocketAddress(4466),datagramPacket.sender()));
            datagramPacket.release();
            ReferenceCountUtil.release(co);
        }
        // TYPE 2： 回发给Proxy-Server

    }

}