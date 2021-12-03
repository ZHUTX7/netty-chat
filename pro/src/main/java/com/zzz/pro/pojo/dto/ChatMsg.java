package com.zzz.pro.pojo.dto;

/**
 * @author ztx
 * @date 2021-12-03 15:23
 * @description :
 */
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : http://www.chiner.pro
 * @date : 2021-12-3
 * @desc : 聊天信息表
 */
@Table(name="chat_msg")
public class ChatMsg implements Serializable,Cloneable{
    /** 消息ID */
    private String msgId ;
    /** 发送人 */
    private String sendUserId ;
    /** 收件人 */
    private String acceptUserId ;
    /** 消息 */
    private String message ;
    /** 消息是否签收 */
    private Integer signFlag ;
    /** 发送时间 */
    private Date sendTime ;

    /** 消息ID */
    public String getMsgId(){
        return this.msgId;
    }
    /** 消息ID */
    public void setMsgId(String msgId){
        this.msgId=msgId;
    }
    /** 发送人 */
    public String getSendUserId(){
        return this.sendUserId;
    }
    /** 发送人 */
    public void setSendUserId(String sendUserId){
        this.sendUserId=sendUserId;
    }
    /** 收件人 */
    public String getAcceptUserId(){
        return this.acceptUserId;
    }
    /** 收件人 */
    public void setAcceptUserId(String acceptUserId){
        this.acceptUserId=acceptUserId;
    }
    /** 消息 */
    public String getMessage(){
        return this.message;
    }
    /** 消息 */
    public void setMessage(String message){
        this.message=message;
    }
    /** 消息是否签收 */
    public Integer getSignFlag(){
        return this.signFlag;
    }
    /** 消息是否签收 */
    public void setSignFlag(Integer signFlag){
        this.signFlag=signFlag;
    }
    /** 发送时间 */
    public Date getSendTime(){
        return this.sendTime;
    }
    /** 发送时间 */
    public void setSendTime(Date sendTime){
        this.sendTime=sendTime;
    }
}