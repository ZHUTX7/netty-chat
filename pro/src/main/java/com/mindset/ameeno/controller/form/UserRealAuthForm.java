package com.mindset.ameeno.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author zhutianxiang
 * @Description 用户真人认证
 * @Date 2023/10/11 16:58
 * @Version 1.0
 */
@Data
public class UserRealAuthForm {
    @NotBlank(message = "用户ID不能为空")
    String userId;
    String realName;
    String idCard;
}
