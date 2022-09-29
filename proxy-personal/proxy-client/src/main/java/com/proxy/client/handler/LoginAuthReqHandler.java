package com.proxy.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.proxy.client.service.ClientBeanManager;
import com.proxy.client.service.ConfigService;
import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.util.MacUtils;
import com.proxy.common.util.ProxyMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 登录安全认证请求 handler
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("proxy-client向网关发送注册信息");
        String proxyClientKey =  ClientBeanManager.getConfigService().readConfig().get("proxyClientKey");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("proxyClientKey",proxyClientKey);
        ctx.writeAndFlush(ProxyMessageUtil.buildLoginReq(null, jsonObject.toJSONString().getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ProxyMessage message = (ProxyMessage) msg;
        // 如果是握手应答消息，需要判`断是否认证成功
        byte type = message.getType();

        if (type == CommonConstant.Login.TYPE_LOGIN_RESP) {
            byte command = message.getCommand()[0];
            String loginMsg = new String(message.getData());

            if (command == CommonConstant.Login.LOGIN_SUCCESS) {
                logger.info("proxy-client认证登录成功");
                // 记录客户端和代理服务器的channel
                ClientBeanManager.getProxyService().setChannel(ctx.channel());
            } else if (command == CommonConstant.Login.LOGIN_FAIL) {
                logger.info("proxy-client认证登录失败:{}", loginMsg);
                // 关闭 or 继续尝试   当前： 继续尝试
                //ctx.channel().close();
            }
        } else {
            //向上传递消息，由其他handler 继续处理
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
    }

}
