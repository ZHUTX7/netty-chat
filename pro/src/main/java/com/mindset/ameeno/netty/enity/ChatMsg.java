package com.mindset.ameeno.netty.enity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ztx
 * @date 2021-12-15 16:35
 * @description :  消息DTO
 */
@Data
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = 3611169682695799175L;
    private String sendUserName;   // 发送者的昵称
    private String senderId;        // 发送者的用户id
    private String receiverId;        // 接受者的用户id
    private String msg;                // 聊天内容
    private String msgId;            // 用于消息的签收
    private Integer msgType;       //消息类型
    private Date sendTime;              //发送时间

}
