package com.zzz.pro.pojo.bo;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UserTagBO {
    private String tag;
    private String userKey;
    private String userValue;
}
