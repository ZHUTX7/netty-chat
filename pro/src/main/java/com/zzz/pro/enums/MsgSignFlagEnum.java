package com.zzz.pro.enums;

/**
 * @author ztx
 * @date 2021-12-03 11:25
 * @description :消息签收状态 枚举
 */
public enum MsgSignFlagEnum {

    unsign(0, "未签收"),
    signed(1, "已签收");

    public final Integer type;
    public final String content;

    MsgSignFlagEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Integer getType() {
        return type;
    }


}
