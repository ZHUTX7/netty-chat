package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.Timestamp;


@Data
@Table(name = "user_base_info")
public class UserBaseInfo {

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 手机号
     */
    @Column(name = "user_phone")
    private String userPhone;

    /**
     * 邮箱
     */
    @Column(name = "user_email")
    private String userEmail;

    /**
     * 密码
     */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * 登录状态
     */
    @Column(name = "user_login_state")
    private Integer userLoginState;

    /**
     * 用户角色 ： 普通用户 VIP SVIP ...
     * 普通用户： 1
     * VIP： 2
     * SVIP ： 3
     */
    @Column(name = "user_role")
    private Integer userRole;

    /**
     * 上次登录时间
     */
    @Column(name = "last_login_time")
    private Timestamp lastLoginTime;

    @Column(name = "user_coordination")
    private String coordination;

}