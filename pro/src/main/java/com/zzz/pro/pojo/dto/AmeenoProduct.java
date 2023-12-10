package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Table(name = "ameeno_product")
public class AmeenoProduct {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(generator="UUID")
    private String productId;

    /**
     * 服务名称
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 服务描述
     */
    @Column(name = "product_desc")
    private String productDesc;

    /**
     * 服务类型
     * "1 = COUNT_SKU_PRODUCT"
     " 2 = DURATION_SKU_PRODUCT"
       3 = VIP_SERVICE
     */
    @Column(name = "product_type")
    private Integer productType;




}