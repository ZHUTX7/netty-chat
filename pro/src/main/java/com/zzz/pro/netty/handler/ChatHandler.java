package com.zzz.pro.netty.handler;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zzz.pro.enums.MsgActionEnum;
import com.zzz.pro.netty.UserChannelMap;
import com.zzz.pro.netty.WSServer;
import com.zzz.pro.netty.dto.ChatMsg;
import com.zzz.pro.netty.enity.DataContent;
import com.zzz.pro.utils.JsonUtils;
import com.zzz.pro.utils.ResponseBuilder;
import com.zzz.pro.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
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
        System.out.println("收到消息:"+content);
        Channel currentChannel = ctx.channel();

        currentChannel.writeAndFlush("copy");

        //-------------TEST----------------

        String sendUserId = Math.random()+"";
        System.out.println(sendUserId);
        ByteBuf msg2 = Unpooled.copiedBuffer("hello,im"+sendUserId, Charset.defaultCharset());
        UserChannelMap.getInstance().put(sendUserId, currentChannel);
        UserChannelMap.output();

        //发送
        Channel channel = UserChannelMap.getInstance().get(content);
        if(UserChannelMap.getInstance().get(content)!=null){
            channel.writeAndFlush(ResponseBuilder.initialResponse(msg2));
        }

        // 1. 获取客户端发来的消息
//        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
//        Integer action = dataContent.getAction();




        // 2. 责任链模式， 判断消息类型
//        if (action == MsgActionEnum.CONNECT.type) {
//            //当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
//            String sendUserId = dataContent.getChatMsg().getSenderId();
//            UserChannelMap.getInstance().put(sendUserId, currentChannel);
//            //测试
//            UserChannelMap.output();
//        } else if (action == MsgActionEnum.CHAT.type) {
//            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
//            ChatMsg chatMsg = dataContent.getChatMsg();
//            String msgText = chatMsg.getMsg();
//            String receiverId = chatMsg.getReceiverId();
//            String senderId = chatMsg.getSenderId();
//
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
