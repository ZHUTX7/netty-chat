package com.zzz.pro.enums;

/**
 * @author ztx
 * @date 2021-12-03 11:23
 * @description :  消息枚举
 */
public enum MsgActionEnum {

    CONNECT(1, "第一次(或重连)初始化连接"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "客户端保持心跳"),
    PULL_FRIEND(5, "拉取通信好友"),
    PULL_USER_LIST(6,"拉取用户信息"),
    GPS(7,"位置数据");

    public final Integer type;
    public final String content;

    MsgActionEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    public Integer getType() {
        return type;
    }
}
