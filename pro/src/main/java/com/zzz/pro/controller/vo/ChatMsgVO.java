package com.zzz.pro.controller.vo;

import lombok.Data;

@Data
public class ChatMsgVO {
    private String msgId;
    private String sendUserId;
    private Integer messageType;
    private String message;
    private Long sendTime;
}
