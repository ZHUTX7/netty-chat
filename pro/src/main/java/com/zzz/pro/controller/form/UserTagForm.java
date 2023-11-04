package com.zzz.pro.controller.form;

import com.zzz.pro.pojo.bo.UserTagBO;
import lombok.Data;

import java.util.List;

@Data
public class UserTagForm {
    private String userId;
    private List<UserTagBO> dataList;
}
