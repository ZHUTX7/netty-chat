package com.mindset.ameeno.controller.form;

import com.mindset.ameeno.pojo.bo.UserTagBO;
import lombok.Data;

import java.util.List;

@Data
public class UserTagForm {
    private String userId;
    private List<UserTagBO> dataList;
}
