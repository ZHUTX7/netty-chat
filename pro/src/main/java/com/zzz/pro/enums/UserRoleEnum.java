package com.zzz.pro.enums;

import lombok.Data;

public enum UserRoleEnum implements CodeEnum<Integer>{
    //普通用户角色


    NORMAL_ROLE(1,"普通用户"),
    VIP_ROLE(2,"VIP用户"),
    SVIP_ROLE(3,"SVIP用户");

    private Integer code;

    private String title;

    UserRoleEnum(Integer code, String title) {
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
