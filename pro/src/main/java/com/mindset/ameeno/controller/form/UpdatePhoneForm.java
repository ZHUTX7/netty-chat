package com.mindset.ameeno.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author zhutianxiang
 
 * @Date 2023/11/16 17:28
 * @Version 1.0
 */
@Data
public class UpdatePhoneForm {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "新手机号不能为空")
    private String newPhone;
    @NotBlank(message = "验证码不能为空")
    private String code;
    private String userId ;
}
