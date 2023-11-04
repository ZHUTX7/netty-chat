package com.zzz.pro.controller.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FriendsVO {
    private String userId;
    private String userNickName;
    private String userImage;
    private Integer friendsStatus;
    private Date  creatTime;
    private int realAuth;
}
