package com.zzz.pro.controller.vo;

import lombok.Data;

@Data
public class ChatWindowsVO {
    //好友基础信息
    private String friendsId;
    private String friendsNickName;
    private String friendsImage;

    //约会状态
    private Integer datingStatus;
    private String datingMsg;

    //好友状态
    private Integer friendsRelStatus;
    private String friendsRelMsg;

}
