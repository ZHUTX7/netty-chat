package com.mindset.ameeno.controller.vo;

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
    private String userRole;
    private int realAuth;
    private String userType = "DEFAULT";

}
