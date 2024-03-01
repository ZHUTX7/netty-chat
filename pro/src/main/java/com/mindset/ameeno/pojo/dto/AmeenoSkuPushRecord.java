package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "ameeno_sku_push_record")
public class AmeenoSkuPushRecord {
    private String id;

    @Column(name = "user_id")
    private String userId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 发货时间
     */
    @Column(name = "push_time")
    private Date pushTime;

    /**
     * 发货状态
     */
    @Column(name = "push_state")
    private Integer pushState;

    /**
     * 交易凭证
     */
    private String receipt;

}