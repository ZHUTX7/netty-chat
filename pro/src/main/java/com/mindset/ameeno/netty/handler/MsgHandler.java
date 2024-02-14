package com.mindset.ameeno.netty.handler;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.mindset.ameeno.enums.MsgActionEnum;
import com.mindset.ameeno.enums.MsgSignFlagEnum;
import com.mindset.ameeno.enums.MsgTypeEnum;
import com.mindset.ameeno.enums.RedisKeyEnum;
import com.mindset.ameeno.netty.UserChannelMap;
import com.mindset.ameeno.utils.*;

import com.mindset.ameeno.netty.enity.ChatMsg;
import com.mindset.ameeno.netty.enity.DataContent;
import com.mindset.ameeno.netty.enity.SystemMsg;
import com.mindset.ameeno.controller.vo.PushMsgVO;
import com.mindset.ameeno.service.ChatMsgService;
import com.mindset.ameeno.service.SensitiveAnalyseService;
import com.mindset.ameeno.service.UserService;
import com.mindset.ameeno.task.Msg2Kafka;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author ztx
 * @date 2021-12-15 16:33
 * @Description: 处理消息的handler
 */
@ChannelHandler.Sharable
public class MsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Logger logger = LoggerFactory.getLogger(MsgHandler.class);
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception {
        String content = msg.text();
        Channel currentChannel = ctx.channel();

        // 1. 获取客户端发来的消息
        //    数据解码解析
        DataContent dataContent = null;

        try{
            dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
            if(dataContent==null){
                return;
            }
        }catch (Exception e){
            logger.error("消息数据格式错误");
            currentChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            "消息数据格式错误,请检查数据"));
            return;
        }
        Integer action = dataContent.getAction();
        if (action == MsgActionEnum.KEEPALIVE.type) {
            return;
        }
        String sendUserId  = dataContent.getUserId();

        // 2. 判断消息类型，根据不同的类型来处理不同的业务
        // 2.1 websocket 第一次连接，用户上线
        if (action == MsgActionEnum.CONNECT.type) {
            handleConnectMsg(dataContent,currentChannel,sendUserId);
        }
        else if (action == MsgActionEnum.CHAT.type) {
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            handleChatMsg(dataContent,currentChannel,sendUserId);
        }
        else if (action == MsgActionEnum.SIGNED.type) {
            handleSignMsg(dataContent);
        }
        //开始拉取用户信息进行匹配
        else if(action == MsgActionEnum.PULL_USER_LIST.type){
            handlePullUserMsg(currentChannel,sendUserId);
        }
        else if(action == MsgActionEnum.GPS.type){
           handleGpsMsg(dataContent);
        }
        else if(action == MsgActionEnum.USER_IS_WRITING.type){
            handleWritingMsg(dataContent);
        }
    }


    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        logger.info("users add"+ctx.channel().id());
        ctx.writeAndFlush("hello , established");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        logger.info("客户端断开，channle对应的长id为："
                + ctx.channel().id().asLongText());
        users.remove(ctx.channel());

    }

    //拷贝dto文件准备落kafka
    private void SaveChatData(ChatMsg chatMsg,int sign){

        logger.info("聊天数据落地kafka");
        com.mindset.ameeno.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
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

    private void handleConnectMsg(DataContent dataContent, Channel currentChannel, String sendUserId) {
        //当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
        UserChannelMap.getInstance().put(sendUserId, currentChannel);
        //测试x
        logger.info("当前在线用户列表为：");
        String userList = UserChannelMap.output();
        logger.info( "<<<测试打印信息>>>[用户]"+sendUserId +"登陆成功，  时间：" +LocalDateTime.now());
        logger.info( "<<<测试打印信息>>>[当前在线用户]：\n"+userList);
        String deviceId = dataContent.getSystemMsg().getDeviceId();
        RedisStringUtil redisStringUtil =   (RedisStringUtil) SpringUtil.getBean("redisStringUtil");
        if(!StringUtils.isEmpty(deviceId)){
            redisStringUtil.set(RedisKeyEnum.USER_DEVICE_ID.getCode()
                    + sendUserId,deviceId);
        }
        //TODO数据库更新deviceId


    }

    private void handleWritingMsg(DataContent dataContent){
        String receiverId = dataContent.getChatMsg().getReceiverId();
        Channel receiveChannel = UserChannelMap.getInstance().get(receiverId);
        if(receiveChannel==null){
            return;
        }
        receiveChannel.writeAndFlush(new TextWebSocketFrame(
                JsonUtils.objectToJson(dataContent)));
    }

    private void handleChatMsg(DataContent dataContent,Channel currentChannel,String sendUserId) throws ExecutionException, InterruptedException {
        dataContent.getChatMsg().setSendTime(new Date());
        if (UserChannelMap.getInstance().get(sendUserId) == null
                || !UserChannelMap.getInstance().get(sendUserId).equals(currentChannel)) {
            currentChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            "senderID与当前用户不匹配"));
            currentChannel.close();
        }
        ChatMsg chatMsg = dataContent.getChatMsg();
        int sign = MsgSignFlagEnum.unsign.getType();
        String msgText = chatMsg.getMsg();
        String receiverId = chatMsg.getReceiverId();
        String senderId = chatMsg.getSenderId();
        Channel receiveChannel = UserChannelMap.getInstance().get(receiverId);
        dataContent.setAction(3);

        //敏内容检测
        SensitiveAnalyseService analyseService = (SensitiveAnalyseService) SpringUtil.getBean("sensitiveAnalyseService");
        if(analyseService.isSensitive(chatMsg)){
            //提示发送方
            chatMsg.setMsg("<敏感信息>");
            chatMsg.setMsgId(chatMsg.getMsgId()+"1");
            chatMsg.setMsgType(MsgTypeEnum.MESSAGE_ALERT.getCode());
            chatMsg.setReceiverId(senderId);
            chatMsg.setSenderId(receiverId);
            currentChannel.writeAndFlush(new TextWebSocketFrame(
                    JsonUtils.objectToJson(dataContent)));
            //信息入库
            chatMsg.setReceiverId(receiverId);
            chatMsg.setSenderId(senderId);
            SaveChatData(chatMsg, MsgSignFlagEnum.signed.getType());
            return;
        }
        // 用户在线
        if (receiveChannel != null && users.find(receiveChannel.id()) != null) {
            try {
                logger.info("<<<测试打印信息>>> 在线消息发送- 发送人：" + senderId + "  发送信息：" + msgText);
                receiveChannel.writeAndFlush(new TextWebSocketFrame(
                        JsonUtils.objectToJson(dataContent)));
                sign = MsgSignFlagEnum.signed.getType();
            } catch (Exception e) {
                logger.error("Message发送给在线用户报错");
            }
        }

        RedisStringUtil redisStringUtil = (RedisStringUtil) SpringUtil.getBean("redisStringUtil");
        PushUtils pushUtils =  (PushUtils) SpringUtil.getBean("pushUtils");
        String dvId = redisStringUtil.get(RedisKeyEnum.USER_DEVICE_ID.getCode() + chatMsg.getReceiverId());
        if (!StringUtils.isEmpty(dvId)) {
            //离线推送
            pushUtils.pushMsg(PushMsgVO.chatMsg2PushMsg(chatMsg),dvId);
        }

        //聊天数据落地
        SaveChatData(chatMsg, sign);
    }

    private void handleSignMsg(DataContent dataContent){
        SystemMsg systemMsg = dataContent.getSystemMsg();
        if(null == systemMsg){
            return;
        }
        if(CollectionUtils.isEmpty(systemMsg.getMsgList())){
            return;
        }
        ChatMsgService chatMsgService  = (ChatMsgService) SpringUtil.getBean("chatMsgService");
        //TODO 这个数据吐kafka消费
        chatMsgService.updateMsgStatus(dataContent.getUserId(),systemMsg.getMsgList());
    }
    private void handlePullUserMsg(Channel currentChannel,String sendUserId) {
//        UserFilterForm userFilterForm = new UserFilterForm();
//        userFilterForm.setMaxAge(100);
//        userFilterForm.setSex(SexEnum.MALE.getCode());
//        userFilterForm.setMinAge(1);
//        userFilterForm.setPos("111");
//
//        // UserFilterForm userFilterForm= JsonUtils.jsonToPojo(dataContent.getExpand(), UserFilterForm.class);
//        MatchService matchService = (MatchService) SpringUtil.getBean("matchService");
//        //进入匹配池  (1次推30人，30人都确认完喜欢不喜欢后，统一调用接口，批量把不喜欢的用户插入黑名单。同时
//        // 客户端继续发送WS请求，服务端接着给用户推）
//        List<UserProfileVO> userProfileVOS =  matchService.pushMatchUserList(userFilterForm,sendUserId);
//        if(CollectionUtils.isEmpty(userProfileVOS)){
//            Map<String,String> map = new HashMap<>();
//            map.put("msg","暂时没有可匹配用户～");
//            currentChannel.writeAndFlush(new TextWebSocketFrame(
//                    JsonUtils.objectToJson(map)));
//        }else {
//            PushUserListBO pushUserListBO = new PushUserListBO();
//            pushUserListBO.setMsg(userProfileVOS);
//            pushUserListBO.setAction(6);
//            currentChannel.writeAndFlush(new TextWebSocketFrame(
//                    JsonUtils.objectToJson(pushUserListBO)));
//        }
    }

    private void handleGpsMsg(DataContent dataContent){
        String userId = dataContent.getUserId();
        double[] gps = dataContent.getSystemMsg().getGps();
        UserService userService =   (UserService) SpringUtil.getBean("userService");
        userService.changeUserGps(userId,gps);
    }
}
