package com.zzz.pro.enums;

import lombok.Data;

public enum UserRoleEnum implements CodeEnum<String>{
    //普通用户角色


    NORMAL_ROLE("NORMAL","普通用户"),
    VIP_ROLE("SVIP","VIP用户"),
    SVIP_ROLE("SSVIP","SVIP用户");

    private String code;

    private String title;

    UserRoleEnum(String code, String title) {
        this.code = code;
        this.title = title;
    }
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
