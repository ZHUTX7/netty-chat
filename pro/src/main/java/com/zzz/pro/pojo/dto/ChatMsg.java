package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "chat_msg")
public class ChatMsg {
    /**
     * 消息ID
     */
    @Column(name = "msg_id")
    private String msgId;

    /**
     * 发送人
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 收件人
     */
    @Column(name = "accept_user_id")
    private String acceptUserId;

    @Column(name = "message_type")
    private Integer messageType;

    /**
     * 消息
     */
    @Column(name = "message")
    private String message;

    /**
     * 消息是否签收
     */
    @Column(name = "sign_flag")
    private Integer signFlag;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private Date sendTime;


}