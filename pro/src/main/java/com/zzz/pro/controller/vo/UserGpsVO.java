package com.zzz.pro.controller.vo;

import lombok.Data;

@Data
public class UserGpsVO {
    private String userId;
    //用户位置
    private String userGps;
    //距离目标地点距离
    private String distance;
}
