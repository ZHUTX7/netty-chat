package com.mindset.ameeno.controller.vo;


import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description 已经匹配的对象
 * @Date 2023/8/8 21:00
 * @Version 1.0
 */
@Data
public class MatchUserVO {
    private String userId;
    private String userNickName;
    private String userImage;
    private Integer birthDate;
    private String userSex;
    private String userHometown;
    private double distance;
    private Long matchTime;
    private Long datingStartTime;
    private Integer status;
    private int isInfinityChat;
}
