package com.zzz.pro.pojo.dto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user_personal_info")
public class UserPersonalInfo {
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户性别 1男 0女
     */
    @Column(name = "user_sex")
    private int userSex;

    /**
     * 用户昵称
     */
    @Column(name = "user_nickname")
    private String userNickname;

    /**
     * 年龄
     */
    @Column(name = "user_gender")
    private Integer userGender;

    /**
     * 个性签名
     */
    @Column(name = "user_motto")
    private String userMotto;

    /**
     * 备注
     */
    @Column(name = "user_memo")
    private String userMemo;

    /**
     * 学历
     */
    @Column(name = "user_education")
    private Integer userEducation;

    /**
     * 职业
     */
    @Column(name = "user_profession")
    private String userProfession;

    /**
     * 星座（不加具体出生年月日）
     */
    @Column(name = "user_constellation")
    private String userConstellation;

    /**
     * 当前位置
     */
    @Column(name = "user_location")
    private String userLocation;

    /**
     * 家乡
     */
    @Column(name = "user_hometown")
    private String userHometown;

    /**
     * 身高
     */
    @Column(name = "user_height")
    private Integer userHeight;

    /**
     * 体重
     */
    @Column(name = "user_weight")
    private Integer userWeight;

    /**
     * 用户头像
     */
    @Column(name = "user_face_image")
    private String userFaceImage;

    /**
     * 用户高清头像
     */
    @Column(name = "user_face_image_big")
    private String userFaceImageBig;

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
     * 获取用户昵称
     *
     * @return user_nickname - 用户昵称
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * 设置用户昵称
     *
     * @param userNickname 用户昵称
     */
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    /**
     * 获取年龄
     *
     * @return user_gender - 年龄
     */
    public Integer getUserGender() {
        return userGender;
    }

    /**
     * 设置年龄
     *
     * @param userGender 年龄
     */
    public void setUserGender(Integer userGender) {
        this.userGender = userGender;
    }

    /**
     * 获取个性签名
     *
     * @return user_motto - 个性签名
     */
    public String getUserMotto() {
        return userMotto;
    }

    /**
     * 设置个性签名
     *
     * @param userMotto 个性签名
     */
    public void setUserMotto(String userMotto) {
        this.userMotto = userMotto;
    }

    /**
     * 获取备注
     *
     * @return user_memo - 备注
     */
    public String getUserMemo() {
        return userMemo;
    }

    /**
     * 设置备注
     *
     * @param userMemo 备注
     */
    public void setUserMemo(String userMemo) {
        this.userMemo = userMemo;
    }

    /**
     * 获取学历
     *
     * @return user_education - 学历
     */
    public Integer getUserEducation() {
        return userEducation;
    }

    /**
     * 设置学历
     *
     * @param userEducation 学历
     */
    public void setUserEducation(Integer userEducation) {
        this.userEducation = userEducation;
    }

    /**
     * 获取职业
     *
     * @return user_profession - 职业
     */
    public String getUserProfession() {
        return userProfession;
    }

    /**
     * 设置职业
     *
     * @param userProfession 职业
     */
    public void setUserProfession(String userProfession) {
        this.userProfession = userProfession;
    }

    /**
     * 获取星座（不加具体出生年月日）
     *
     * @return user_constellation - 星座（不加具体出生年月日）
     */
    public String getUserConstellation() {
        return userConstellation;
    }

    /**
     * 设置星座（不加具体出生年月日）
     *
     * @param userConstellation 星座（不加具体出生年月日）
     */
    public void setUserConstellation(String userConstellation) {
        this.userConstellation = userConstellation;
    }

    /**
     * 获取当前位置
     *
     * @return user_location - 当前位置
     */
    public String getUserLocation() {
        return userLocation;
    }

    /**
     * 设置当前位置
     *
     * @param userLocation 当前位置
     */
    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    /**
     * 获取家乡
     *
     * @return user_hometown - 家乡
     */
    public String getUserHometown() {
        return userHometown;
    }

    /**
     * 设置家乡
     *
     * @param userHometown 家乡
     */
    public void setUserHometown(String userHometown) {
        this.userHometown = userHometown;
    }

    /**
     * 获取身高
     *
     * @return user_height - 身高
     */
    public Integer getUserHeight() {
        return userHeight;
    }

    /**
     * 设置身高
     *
     * @param userHeight 身高
     */
    public void setUserHeight(Integer userHeight) {
        this.userHeight = userHeight;
    }

    /**
     * 获取体重
     *
     * @return user_weight - 体重
     */
    public Integer getUserWeight() {
        return userWeight;
    }

    /**
     * 设置体重
     *
     * @param userWeight 体重
     */
    public void setUserWeight(Integer userWeight) {
        this.userWeight = userWeight;
    }

    /**
     * 获取用户头像
     *
     * @return user_face_image - 用户头像
     */
    public String getUserFaceImage() {
        return userFaceImage;
    }

    /**
     * 设置用户头像
     *
     * @param userFaceImage 用户头像
     */
    public void setUserFaceImage(String userFaceImage) {
        this.userFaceImage = userFaceImage;
    }

    /**
     * 获取用户高清头像
     *
     * @return user_face_image_big - 用户高清头像
     */
    public String getUserFaceImageBig() {
        return userFaceImageBig;
    }

    /**
     * 设置用户高清头像
     *
     * @param userFaceImageBig 用户高清头像
     */
    public void setUserFaceImageBig(String userFaceImageBig) {
        this.userFaceImageBig = userFaceImageBig;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }
}