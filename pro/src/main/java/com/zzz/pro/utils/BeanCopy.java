package com.zzz.pro.utils;

import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

// ChatMsg to ChatMsgDTO
public class BeanCopy {
    private static SnowFlake snowFlake = new SnowFlake(1,1);

    public static ChatMsg copy(com.zzz.pro.netty.dto.ChatMsg msg){
        ChatMsg dto = new ChatMsg();
        dto.setMsgId(snowFlake.nextId());
        dto.setMessage(msg.getMsg());
        dto.setAcceptUserId(msg.getReceiverId());
        dto.setMessageType(msg.getMsgType());
        dto.setSendUserId(msg.getSenderId());
        dto.setSendTime(msg.getSendTime());
        return dto;
        //消息是否签收不发送
    }
}
