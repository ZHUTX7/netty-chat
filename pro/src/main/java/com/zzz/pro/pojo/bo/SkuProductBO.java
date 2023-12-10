package com.zzz.pro.pojo.bo;

import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description SkuProductBO
 * @Date 2023/11/6 19:21
 * @Version 1.0
 */
@Data
public class SkuProductBO {
    private String skuId;
    private String productId;
    private Integer nums = 0;
    private Integer timeLimit;
    private String timeUnit;
    private Integer productType;
}
