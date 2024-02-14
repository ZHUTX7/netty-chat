package com.mindset.ameeno.controller.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LoginResultVO {
    private String userId;
    private String userPhone;
    private String userRole;
    private Date lastLoginTime;
    //是否为新用户
    private Integer isNewUser = 0;
    private String token;
    private String refreshToken;

}
