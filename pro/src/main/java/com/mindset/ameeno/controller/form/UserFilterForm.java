package com.mindset.ameeno.controller.form;

import lombok.Data;

//用户筛选条件
@Data
public class UserFilterForm {
    private String sex;
    private Integer minAge;
    private Integer maxAge;
    private String pos;
}
