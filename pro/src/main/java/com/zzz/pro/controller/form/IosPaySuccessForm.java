package com.zzz.pro.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author zhutianxiang
 * @Description Ios Pay Success Form
 * @Date 2023/10/30 14:52
 * @Version 1.0
 */
@Data
public class IosPaySuccessForm {
    @NotEmpty(message = "receipt can not be empty" )
    private String receipt;
    @NotEmpty(message = "orderId can not be empty" )
    private String orderId;
}


