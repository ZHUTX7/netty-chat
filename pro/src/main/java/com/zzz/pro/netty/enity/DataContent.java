package com.zzz.pro.netty.enity;

import com.zzz.pro.netty.dto.ChatMsg;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = 8021381444738260454L;

    private Integer action;        // 动作类型
    private ChatMsg chatMsg;    // 用户的聊天内容entity
    private String expand;        // 扩展字段
    private String token;        // 扩展字段
    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
