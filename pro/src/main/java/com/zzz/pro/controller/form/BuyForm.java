package com.zzz.pro.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author zhutianxiang
 * @Description 购买表单
 * @Date 2023/10/25 20:12
 * @Version 1.0
 */

@Data
public class BuyForm {
    @NotEmpty
    private String userId;
    @NotNull
    private String skuId;
    @NotNull
    private Integer buyCount;
}
