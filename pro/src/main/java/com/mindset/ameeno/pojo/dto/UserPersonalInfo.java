package com.mindset.ameeno.pojo.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_personal_info")
@Component
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
    private String userSex;

    /**
     * 用户昵称
     */
    @Column(name = "user_nickname")
    private String userNickname;

    /**
     * 出生日期
     */
    @Column(name = "user_birthdate")
    private Integer userBirthdate;

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

    @Column(name = "real_auth")
    private int realAuth;
}