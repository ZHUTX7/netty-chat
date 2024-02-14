package com.mindset.ameeno.netty.enity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataContent implements Serializable {

    private static final long serialVersionUID = 8021381444738260454L;
    // 动作类型
    private Integer action;
    // 用户的聊天内容entity
    private ChatMsg chatMsg;
    // 扩展字段
    private SystemMsg systemMsg;
    // 扩展字段
    private String token;
    private String userId;

}
