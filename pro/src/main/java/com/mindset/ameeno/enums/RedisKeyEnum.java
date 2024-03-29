package com.mindset.ameeno.enums;

/**
 * @author ztx
 * @date 2022-01-11 15:42
 * @description :
 */

public enum RedisKeyEnum implements CodeEnum {
    USER_ONLINE_SUM("ur:onLienSum","在线用户数"),
    ALL_USER_VO("ur:vo","所有用户VO信息"),
    USER_INFO("ur:urInfo","用户信息"),
    USER_POSITION("ur:pos:","用户位置"),
    USER_DISTANCE("ur:distance:","用户距离目标地点地址"),
    BOYS_WAITING_POOL("match:boys_waitingPool:","待匹配用户池"),
    GIRLS_WAITING_POOL("match:girls_waitingPool:","待匹配用户池"),
    DISLIKE_USER_POOL("match:dislikePool:","黑名单"),
    MATCH_SELECTED_POOL("match:waitingPool:","对冲池"),
    MATCH_SUPER_LIKE_POOL("match:superLike:","超级喜欢"),
    USER_DEVICE_ID("devId:","用户设备ID"),
    USER_UNREAD_MSG_COUNT("ur:unread:","用户未读消息条数"),

    USER_UN_MATCH_LIST("pool:matchable:","推荐用户匹配池"),

    ALL_USER_POOL("pool:all","a"),
    USER_BLACK_POOL("pool:ur:","a"),
    USER_SELECTED_POOL("sel","a")

    ;
    private String code;

    private String title;

    RedisKeyEnum(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
