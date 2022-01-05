package com.zzz.pro.pojo.dto;

import javax.persistence.Column;
import javax.persistence.Table;

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
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取手机号
     *
     * @return user_phone - 手机号
     */
    public String getUserPhone() {
        return userPhone;
    }

    /**
     * 设置手机号
     *
     * @param userPhone 手机号
     */
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    /**
     * 获取邮箱
     *
     * @return user_email - 邮箱
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * 设置邮箱
     *
     * @param userEmail 邮箱
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * 获取密码
     *
     * @return user_password - 密码
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * 设置密码
     *
     * @param userPassword 密码
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * 获取登录状态
     *
     * @return user_login_state - 登录状态
     */
    public Integer getUserLoginState() {
        return userLoginState;
    }

    /**
     * 设置登录状态
     *
     * @param userLoginState 登录状态
     */
    public void setUserLoginState(Integer userLoginState) {
        this.userLoginState = userLoginState;
    }
}