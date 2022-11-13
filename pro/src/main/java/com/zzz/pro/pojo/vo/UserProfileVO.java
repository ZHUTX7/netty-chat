package com.zzz.pro.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileVO implements Serializable {
    private static final long serialVersionUID = -1L;
    private String userId;
    private String userNickname;
    private String userFaceImage;
    private Integer userSex;
    private Integer userGender;
    private String userMotto;
    private String userLocation;
}
