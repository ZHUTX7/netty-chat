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
    private Long msgId;

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
    private int messageType;

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

    /**
     * 获取消息ID
     *
     * @return msg_id - 消息ID
     */
    public Long getMsgId() {
        return msgId;
    }

    /**
     * 设置消息ID
     *
     * @param msgId 消息ID
     */
    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    /**
     * 获取发送人
     *
     * @return send_user_id - 发送人
     */
    public String getSendUserId() {
        return sendUserId;
    }

    /**
     * 设置发送人
     *
     * @param sendUserId 发送人
     */
    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    /**
     * 获取收件人
     *
     * @return accept_user_id - 收件人
     */
    public String getAcceptUserId() {
        return acceptUserId;
    }

    /**
     * 设置收件人
     *
     * @param acceptUserId 收件人
     */
    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    /**
     * 获取消息
     *
     * @return message - 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     *
     * @param message 消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取消息是否签收
     *
     * @return sign_flag - 消息是否签收
     */
    public Integer getSignFlag() {
        return signFlag;
    }

    /**
     * 设置消息是否签收
     *
     * @param signFlag 消息是否签收
     */
    public void setSignFlag(Integer signFlag) {
        this.signFlag = signFlag;
    }

    /**
     * 获取发送时间
     *
     * @return send_time - 发送时间
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * 设置发送时间
     *
     * @param sendTime 发送时间
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}