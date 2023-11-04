package com.zzz.pro.enums;


public enum FriendsStatusEnum implements CodeEnum<Integer>{
    //好友关系类型
    BOTH_FRIENDS(1,"双向好友"),
    SINGLE_FRIENDS_(2,"单向好友-删对方"),
    SINGLE_FRIENDS_ME(3,"单向好友-被删"),
    BOTH_DEL_FRIENDS(4,"双删");

    private Integer code;

    private String title;

    FriendsStatusEnum(Integer code, String title) {
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
