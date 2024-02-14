package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "ameeno_orders")
public class AmeenoOrders {
    /**
     * 订单id
     */
    @Id
    @Column(name = "order_id")
    private String orderId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 订单创建时间
     */
    @Column(name = "order_create_time")
    private Date orderCreateTime;

    /**
     * 订单更新时间
     */
    @Column(name = "order_update_time")
    private Date orderUpdateTime;

    /**
     * 订单完成时间
     */
    @Column(name = "order_finished_time")
    private Date orderFinishedTime;

    /**
     * 总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 消费商品
     */
    @Column(name = "buy_sku_id")
    private String buySkuId;

    /**
     * 购买数量
     */
    @Column(name = "buy_nums")
    private Integer buyNums;

    /**
     * 订单状态
     */
    @Column(name = "payment_status")
    private String paymentStatus;

    /**
     * 支付方式
     */
    @Column(name = "payment_method")
    private String paymentMethod;

    /**
     * 支付时间
     */
    @Column(name = "payment_date")
    private Date paymentDate;

    /**
     * 支付订单ID 
     */
    @Column(name = "payment_id")
    private String paymentId;

}