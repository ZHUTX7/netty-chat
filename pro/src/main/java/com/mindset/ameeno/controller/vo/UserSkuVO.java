package com.mindset.ameeno.controller.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/27 21:12
 * @Version 1.0
 */
@Data
public class UserSkuVO {
    String productId;
    String productName;
    int productCount;
    Long expireTime;
    int timeLimitFlag;
}
