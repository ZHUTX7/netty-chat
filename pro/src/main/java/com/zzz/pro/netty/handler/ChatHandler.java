package com.zzz.pro.netty.handler;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zzz.pro.enums.MsgActionEnum;
import com.zzz.pro.enums.MsgSignFlagEnum;
import com.zzz.pro.netty.UserChannelMap;
import com.zzz.pro.netty.WSServer;
import com.zzz.pro.netty.dto.ChatMsg;
import com.zzz.pro.netty.enity.DataContent;
import com.zzz.pro.service.ChatMsgService;
import com.zzz.pro.task.Msg2Kafka;
import com.zzz.pro.utils.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ztx
 * @date 2021-12-15 16:33
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Logger logger = LoggerFactory.getLogger(ChatHandler.class);


    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception {
        InetSocketAddress sa = (InetSocketAddress)ctx.channel().remoteAddress();

        logger.warn("获取到客户端{}请求连接:",sa.getAddress().getHostAddress());
        String content = msg.text();
        Channel currentChannel = ctx.channel();

        currentChannel.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器在]" + LocalDateTime.now()
                                + "接受到消息, 消息为：" + content));

        // 1. 获取客户端发来的消息
        DataContent dataContent = null;
        try{
            dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
            if(dataContent==null){
                throw new Exception() ;
            }
        }catch (Exception e){
            logger.warn("消息数据格式错误");
            currentChannel.writeAndFlush(
                    new TextWebSocketFrame(
                           "消息数据格式错误,请检查数据"));
            return;
        }

        Integer action = dataContent.getAction();
        String sendUserId = dataContent.getChatMsg().getSenderId();

        // 2. 判断消息类型，根据不同的类型来处理不同的业务
        if (action == MsgActionEnum.CONNECT.type) {
            //当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来

            UserChannelMap.getInstance().put(sendUserId, currentChannel);
            //测试
            logger.info("当前在线用户列表为：");
            String userList = UserChannelMap.output();
            currentChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            "<<<测试打印信息>>>[用户]"+sendUserId +"登陆成功，  时间：" +LocalDateTime.now()));
            currentChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            "<<<测试打印信息>>>[当前在线用户]：\n"+userList));


        }
        else if (action == MsgActionEnum.CHAT.type) {
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            if(UserChannelMap.getInstance().get(sendUserId) == null
                    || !UserChannelMap.getInstance().get(sendUserId).equals(currentChannel)){
                currentChannel.writeAndFlush(
                        new TextWebSocketFrame(
                                "senderID与当前用户不匹配"));
                currentChannel.close();
            }
            ChatMsg chatMsg = dataContent.getChatMsg();
            int sign = MsgSignFlagEnum.unsign.getType();
            System.out.printf("type is "+chatMsg.getMsgType()+"");
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();
            Channel receiveChannel = UserChannelMap.getInstance().get(receiverId);
            dataContent.setAction(3);

            // 1 - 用户不在线
            if(receiveChannel == null){

                // TODO 调用手机推送
                currentChannel.writeAndFlush(
                        new TextWebSocketFrame(
                              "收信用户不在线"));
                logger.info("收信用户不在线");
            }
            // 2 - 用户在线
            else {
                try {
                    receiveChannel.writeAndFlush(new TextWebSocketFrame(
                            "<<<测试打印信息>>>  发送人："+senderId+"  发送信息：" + msgText));
                    receiveChannel.writeAndFlush(new TextWebSocketFrame(
                            JsonUtils.objectToJson(dataContent)));
                    sign = MsgSignFlagEnum.signed.getType();
                }catch (Exception e){
                    logger.error("Message发送给在线用户报错");
                }

            }
            //聊天数据落地
            SaveChatData(chatMsg,sign);

        }
        else if (action == MsgActionEnum.SIGNED.type) {
            List<String> ids = JsonUtils.jsonToPojo(dataContent.getExpand(), List.class);
            ChatMsgService chatMsgService  = (ChatMsgService) SpringUtil.getBean("chatMsgServiceImpl");
            chatMsgService.updateMsgStatus(ids);


        }else if (action == MsgActionEnum.KEEPALIVE.type) {
            //  2.4  心跳类型的消息
//            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }

    }


    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        // 将用户ID与 channel ID绑定
        ctx.channel().id().asLongText();
        ctx.writeAndFlush("hello , established");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//		clients.remove(ctx.channel());
        logger.info("客户端断开，channle对应的长id为："
                + ctx.channel().id().asLongText());

    }

    //拷贝dto文件准备落kafka
    private void SaveChatData(ChatMsg chatMsg,int sign){

        com.zzz.pro.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
        dto.setSignFlag(sign);
        Msg2Kafka msg2Kafka  = (Msg2Kafka) SpringUtil.getBean("msg2Kafka");
        msg2Kafka.asyncSend(dto);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
