package com.zzz.pro.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author zhutianxiang
 * @Description 约会评分表单
 * @Date 2023/8/22 00:18
 * @Version 1.0
 */

@Data
public class DatingScoreForm {
    //自己id
    private String userId;
    //对方ID
    @NotEmpty(message = "对方ID不能为空")
    private String targetId;
    //评价
    private String evaluate;
    //分数
    private float score;
}
