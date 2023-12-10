package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "ameeno_sku_product")
public class AmeenoSkuProduct {
    @Id
    @GeneratedValue(generator="UUID")
    private Integer id;

    @Column(name = "sku_id")
    private String skuId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_num")
    private Integer productNum;

    /**
     * 是否有时间限制：>1 有 -1 无
     */
    @Column(name = "time_limit")
    private Integer timeLimit;

    /**
     * 时间单位
     */
    @Column(name = "time_unit")
    private String timeUnit;
}