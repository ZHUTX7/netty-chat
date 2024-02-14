package com.mindset.ameeno.utils;

import com.mindset.ameeno.netty.enity.ChatMsg;

// ChatMsg to ChatMsgDTO
public class BeanCopy {
    public static com.mindset.ameeno.pojo.dto.ChatMsg copy(ChatMsg msg){
        com.mindset.ameeno.pojo.dto.ChatMsg dto = new com.mindset.ameeno.pojo.dto.ChatMsg();
        dto.setMsgId(msg.getMsgId());
        dto.setMessage(msg.getMsg());
        dto.setAcceptUserId(msg.getReceiverId());
        dto.setMessageType(msg.getMsgType());
        dto.setSendUserId(msg.getSenderId());
        dto.setSendTime(msg.getSendTime());
        return dto;
    }
}
