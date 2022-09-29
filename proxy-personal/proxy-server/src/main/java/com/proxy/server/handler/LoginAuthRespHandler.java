package com.proxy.server.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.proxy.common.entity.server.ClientNode;

import com.proxy.common.protobuf.ProxyMessage;
import com.proxy.common.protocol.CommonConstant;
import com.proxy.common.util.ProxyMessageUtil;
import com.proxy.common.util.RpcUtils;
import com.proxy.server.ProxyServer;
import com.proxy.server.dao.GatewayRedisDAO;
import com.proxy.server.dto.GatewayRouteDTO;
import com.proxy.server.service.ServerBeanManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录安全认证响应 handler
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {


    private static Logger logger = LoggerFactory.getLogger(LoginAuthRespHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof ProxyMessage) {
            ProxyMessage message = (ProxyMessage) msg;

            //获取消息类型
            byte type = message.getType();


            //如果是请求登录消息
            if (type == CommonConstant.Login.TYPE_LOGIN_REQ) {

                Channel userChannel = ctx.channel();
                InetSocketAddress sa = (InetSocketAddress) userChannel.remoteAddress();

                //处理连接逻辑代码
                // 1.验证Ip地址
                logger.info("代理客户端proxy-client({}),ip:({})请求登录认证", sa.getHostName(),sa.getAddress().getHostAddress());
                //2.验证，暂时忽略


                String json = new String(message.getData());
                JSONObject jsonObject = JSON.parseObject(json);


                // 3.proxyClientKey
                //   并将配置信息加入到内存中
                String clientKey = jsonObject.getString("proxyClientKey");
                ClientNode clientNode = null;

                if(clientKey==null || "".equals(clientKey)){
                    logger.info("proxy-client({})注册失败:clientKey为空！", sa.getHostName());
                    String loginMsg = "proxy-client({})注册失败:clientKey为空！";
                    loginRespone(ctx, loginMsg, CommonConstant.Login.LOGIN_FAIL);
                    closeChannle(ctx);
                    return;
                }

                clientNode =   ProxyServer.addClient(GatewayRedisDAO.queryGatewayRout(clientKey));

                if(clientNode == null ||clientNode.getServerPort2RealServer().isEmpty()){
                    logger.info("网关({})注册失败:网关尚未注册或被禁止登录", sa.getHostName());
                    String loginMsg = "登录失败:网关尚未注册或被禁止登录";
                    loginRespone(ctx, loginMsg, CommonConstant.Login.LOGIN_FAIL);
                    closeChannle(ctx);
                }

                // 4. 生成ClientNode
                if (clientNode != null && clientNode.getStatus() == CommonConstant.ClientStatus.ONLINE) {

                    if (sa.getHostName().equals(clientNode.getHost())) {
                        //同一个客户端再次登录
                        //关闭连接
                        closeChannle(ctx);
                        return;
                    }

                    //已经存在一个相同key的客户端登录了
                    String loginMsg = "登录失败:已经存在一个相同key的proxy-client注册到了该proxy-server";
                    loginRespone(ctx, loginMsg, CommonConstant.Login.LOGIN_FAIL);
                    closeChannle(ctx);
                    return;
                }

                if (clientNode != null && clientNode.getStatus() != CommonConstant.ClientStatus.FORBIDDEN) {
                    //登录响应
                    loginRespone(ctx, "登录成功", CommonConstant.Login.LOGIN_SUCCESS);
                    //保存客户端信息
                    logger.info("生成ClientNode , key = " + clientNode.getClientKey());
                    saveClient2Cache(clientNode, ctx, message);
                    logger.info("client({})登录成功", sa.getHostName());

                    // 保存更新后的代理端口信息
                    logger.info("更新后的路由信息回送给控制器");

                } else {
                    logger.info("网关({})注册失败:网关尚未注册或被禁止登录", sa.getHostName());
                    String loginMsg = "登录失败:网关尚未注册或被禁止登录";
                    loginRespone(ctx, loginMsg, CommonConstant.Login.LOGIN_FAIL);
                    closeChannle(ctx);
                }

            } else {
                ctx.fireChannelRead(msg);
            }

        } else {
            //错误的消息格式
            //关闭用户连接
            closeChannle(ctx);
        }

    }


    /**
     * 保存or更新 client到内存,同时启动代理服务
     */
    private  Map<String,Integer> saveClient2Cache(ClientNode client, ChannelHandlerContext ctx, ProxyMessage message) {

        ctx.channel().attr(CommonConstant.ServerChannelAttributeKey.CLIENT_KEY).set(client.getClientKey());
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().remoteAddress();
        client.setHost(sa.getAddress().getHostName());
        client.setPort(sa.getPort());
        client.setChannel(ctx.channel());
        client.setStatus(CommonConstant.ClientStatus.ONLINE);

        /**
         * 当客户端连接成功后(可能重启),把以前存在的用户连接关闭掉
         */
        Map<Long, Channel> sessionIDTOChannel = ServerBeanManager.getUserSessionService().getAll();
        for (Map.Entry<Long, Channel> entry : sessionIDTOChannel.entrySet()) {
            String tempClientKey = ServerBeanManager.getUserSessionService().getClientKey(entry.getValue());
            if (client.getClientKey().equals(tempClientKey)) {
                //从集合移除
                ServerBeanManager.getUserSessionService().remove(entry.getKey());
                //关闭用户连接
                entry.getValue().close();
                logger.info("{}:关闭失效的用户端连接", client.getClientKey());
            }
        }

        //添加客户端到代理服务集合
        //同时加载路由信息，开启代理端口
        Map<String,Integer> map = ServerBeanManager.getClientService().add(client);
        return map;
    }

    /**
     * 登录响应
     */
    private void loginRespone(ChannelHandlerContext ctx, String msg, byte loginResult) {

        ProxyMessage loginResp = ProxyMessageUtil.buildLoginResp(new byte[]{loginResult}, msg.getBytes());
        ctx.writeAndFlush(loginResp);
    }

    private void closeChannle(ChannelHandlerContext ctx) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            Channel userChannel = ctx.channel();
            InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
            logger.info("网关({})认证失败或者连接异常", sa.getHostName());
            ctx.channel().close();
        }

    }
}
