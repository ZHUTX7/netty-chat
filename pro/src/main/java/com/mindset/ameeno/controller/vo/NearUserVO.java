package com.mindset.ameeno.controller.vo;

import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description 附近的人VO
 * @Date 2023/8/8 21:01
 * @Version 1.0
 */
@Data
public class NearUserVO {
    private String userId;
    private String userNickName;
    private String userImage;
    private Integer birthDate;
    private String userSex;
    private String userHometown;
    private double distance;
    private int superLike;
}
