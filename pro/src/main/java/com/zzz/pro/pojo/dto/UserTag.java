package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "user_tag")
public class UserTag {
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "tag")
    private String tag;
    @Column(name = "user_key")
    private String userKey;
    @Column(name = "user_value")
    private String userValue;
}
