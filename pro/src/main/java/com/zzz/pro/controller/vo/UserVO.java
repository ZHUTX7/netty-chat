package com.zzz.pro.controller.vo;

import lombok.Data;

/**
 * @author ztx
 * @date 2022-01-11 16:47
 * @description :
 */
@Data
public class UserVO {
    private String userId;
    private String userNickName;
    private String userImage;
    private Integer birthDate;
    private String userSex;
    private String userHometown;
    private int realAuth;

}
