package com.zzz.pro.netty.handler;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zzz.pro.enums.MsgActionEnum;
import com.zzz.pro.netty.UserChannelMap;
import com.zzz.pro.netty.WSServer;
import com.zzz.pro.netty.dto.ChatMsg;
import com.zzz.pro.netty.enity.DataContent;
import com.zzz.pro.task.Message2KafkaTask;
import com.zzz.pro.task.TaskExecutor;
import com.zzz.pro.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import io.netty.util.internal.StringUtil;
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

    // TODO 2 从Mysql获取未消费数据（离线消息）传给手机客户端
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

        // 2. 验证token
//        String token = dataContent.getToken();
//        if(StringUtil.isNullOrEmpty(token)||!"123".equals(token)){
//            currentChannel.writeAndFlush(
//                    new TextWebSocketFrame(
//                            "没有附带token值，测试阶段token值为123"));
//            currentChannel.close();
//            return;
//        }
//        HashMap<String,Object> verifyMap = JWTUtils.verify(token);
//        if((Integer) verifyMap.get("token_code")!=1){
//            currentChannel.writeAndFlush(
//                    new TextWebSocketFrame(
//                            "token无效"));
//            currentChannel.close();
//            return;
//        }
//        String sendUserId = JWTUtils.getClaim(token,"userId");
        String sendUserId = dataContent.getChatMsg().getSenderId();

        // 3. 判断消息类型，根据不同的类型来处理不同的业务
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


        } else if (action == MsgActionEnum.CHAT.type) {
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            if(UserChannelMap.getInstance().get(sendUserId) == null
                    || !UserChannelMap.getInstance().get(sendUserId).equals(currentChannel)){
                currentChannel.writeAndFlush(
                        new TextWebSocketFrame(
                                "senderID与当前用户不匹配"));
                currentChannel.close();
            }
            ChatMsg chatMsg = dataContent.getChatMsg();



            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();
            Channel receiveChannel = UserChannelMap.getInstance().get(receiverId);
            dataContent.setAction(3);
            if(receiveChannel == null){
                //拷贝dto文件准备落kafka
                com.zzz.pro.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
                dto.setSignFlag(0);
                TaskExecutor.submit(new Message2KafkaTask(dto));

                currentChannel.writeAndFlush(
                        new TextWebSocketFrame(
                              "当前用户不在线"));
            }

            else {
                receiveChannel.writeAndFlush(new TextWebSocketFrame(
                      "<<<测试打印信息>>>  发送人："+senderId+"  发送信息：" + msgText));
                receiveChannel.writeAndFlush(new TextWebSocketFrame(
                       JsonUtils.objectToJson(dataContent)));

                //拷贝dto文件准备落kafka
                com.zzz.pro.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
                dto.setSignFlag(0);
                TaskExecutor.submit(new Message2KafkaTask(dto));
            }


        }

//
//            // TODO : 保存消息到数据库，并且标记为 未签收
//            {
//                //xxxxxxxxxx
//            }
//            // 2.3 判断收件人是否在线
//            // 发送消息
//            // TODO : 从全局用户Channel关系中获取接受方的channel
//            Channel receiveChannel = UserChannelMap.getInstance().get(receiverId);
//
//            // 2.4 不在线的话...
//
//        }
//          else if (action == MsgActionEnum.SIGNED.type) {
//            // TODO : 签收消息
//        } else if (action == MsgActionEnum.KEEPALIVE.type) {
//            //  2.4  心跳类型的消息
//            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
//        }


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
        System.out.println("客户端断开，channle对应的长id为："
                + ctx.channel().id().asLongText());
        System.out.println("客户端断开，channle对应的短id为："
                + ctx.channel().id().asShortText());
    }

}
