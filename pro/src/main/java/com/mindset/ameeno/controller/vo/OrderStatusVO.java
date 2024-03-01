package com.mindset.ameeno.controller.vo;

import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description
 * @Date 2023/12/13 22:25
 * @Version 1.0
 */
@Data
public class OrderStatusVO {
    private String userId;
    private String orderId;
    private String paymentStatus;

}
