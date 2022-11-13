package com.zzz.pro.enums;

/**
 * @author ztx
 * @date 2022-01-11 15:42
 * @description :
 */

public enum RedisKeyEnum implements CodeEnum {
    USER_ONLINE_SUM("user:onLienSum","在线用户数"),
    USER_INFO("user:userInfo","用户信息"),
    USER_POSITION("user:position","用户位置"),
    BOYS_WAITING_POOL("match:boys_waitingPool","待匹配用户池"),
    GIRLS_WAITING_POOL("match:girls_waitingPool","待匹配用户池"),
    MATCH_SELECTED_POOL("match:waitingPool:","对冲池")
    ;
    private String code;

    private String title;

    RedisKeyEnum(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public Object getCode() {
        return this.code;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
