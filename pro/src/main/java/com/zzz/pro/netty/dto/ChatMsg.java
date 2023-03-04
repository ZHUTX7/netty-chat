package com.zzz.pro.netty.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ztx
 * @date 2021-12-15 16:35
 * @description :  消息DTO
 */
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = 3611169682695799175L;


    private String senderId;        // 发送者的用户id
    private String receiverId;        // 接受者的用户id
    private String msg;                // 聊天内容
    private String msgId;            // 用于消息的签收
//    1 -  'TEXT'
//     2 -        'IMAGE'
//     3 -       'VIDEO'
//     4 -      'VOICE'
    private Integer msgType;
    private Date sendTime;              //发送时间

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }



}
