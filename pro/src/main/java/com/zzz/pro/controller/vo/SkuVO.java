package com.zzz.pro.controller.vo;

import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2023/10/27 21:12
 * @Version 1.0
 */
@Data
public class SkuVO {
    int productId;
    String productName;
    int productCount;
    Long expireTime;
}
