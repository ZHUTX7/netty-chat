package com.zzz.pro.utils;

import com.zzz.pro.pojo.dto.ChatMsg;

// ChatMsg to ChatMsgDTO
public class BeanCopy {
    public static ChatMsg copy(com.zzz.pro.netty.enity.ChatMsg msg){
        ChatMsg dto = new ChatMsg();
        dto.setMsgId(msg.getMsgId());
        dto.setMessage(msg.getMsg());
        dto.setAcceptUserId(msg.getReceiverId());
        dto.setMessageType(msg.getMsgType());
        dto.setSendUserId(msg.getSenderId());
        dto.setSendTime(msg.getSendTime());
        return dto;
    }
}
