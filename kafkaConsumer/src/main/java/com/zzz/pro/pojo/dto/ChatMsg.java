package com.zzz.pro.pojo.dto;



import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

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

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(Integer signFlag) {
        this.signFlag = signFlag;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}