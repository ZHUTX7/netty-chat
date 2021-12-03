package com.zzz.pro.pojo.dto;

/**
 * @author ztx
 * @date 2021-12-03 15:27
 * @description :用户基础信息
 */

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="user_base_info")
public class UserBaseInfo implements Serializable,Cloneable{
    /** 用户ID */
    private String userId ;
    /** 手机号 */
    private String userPhone ;
    /** 邮箱 */
    private String userEmail ;
    /** 密码 */
    private String userPassword ;
    /** 登录状态 */
    private Integer userLoginState ;

    /** 用户ID */
    public String getUserId(){
        return this.userId;
    }
    /** 用户ID */
    public void setUserId(String userId){
        this.userId=userId;
    }
    /** 手机号 */
    public String getUserPhone(){
        return this.userPhone;
    }
    /** 手机号 */
    public void setUserPhone(String userPhone){
        this.userPhone=userPhone;
    }
    /** 邮箱 */
    public String getUserEmail(){
        return this.userEmail;
    }
    /** 邮箱 */
    public void setUserEmail(String userEmail){
        this.userEmail=userEmail;
    }
    /** 密码 */
    public String getUserPassword(){
        return this.userPassword;
    }
    /** 密码 */
    public void setUserPassword(String userPassword){
        this.userPassword=userPassword;
    }
    /** 登录状态 */
    public Integer getUserLoginState(){
        return this.userLoginState;
    }
    /** 登录状态 */
    public void setUserLoginState(Integer userLoginState){
        this.userLoginState=userLoginState;
    }
}