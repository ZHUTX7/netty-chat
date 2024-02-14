package com.mindset.ameeno.enums;

public enum NotifyEnum implements CodeEnum{

    //用户层通知信息 10001开头
    MATCHED_MSG(10001, "您有一个匹配用户"),

    UNREAD_MSG(10009,"有未读信息"),
    RELEASE_RELATION_MSG(10008,"解除匹配"),

    //业务层通知信息 2开头
    ACTIVITY_MSG(20001, "第一次(或重连)初始化连接"),
    ADVERTISEMENT_MSG(2001, "聊天消息"),

    //系统通知消息
    UPDATE_APP_MSG(3009,"APP需要更新"),

    GENERAL_MSG(3001,"通用系统信息");

    private Integer code;

    private String title;

    NotifyEnum(Integer code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
