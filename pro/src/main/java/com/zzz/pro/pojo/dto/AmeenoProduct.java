package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "ameeno_product")
public class AmeenoProduct {
    @Id
    @Column(name = "product_id")
    private Integer productId;

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
     * 服务单价
     */
    @Column(name = "product_price")
    private BigDecimal productPrice;

    /**
     * 服务类型
     * "1 = COUNT_SKU_PRODUCT"
     " 2 = DURATION_SKU_PRODUCT"
       3 = VIP_SERVICE
     */
    @Column(name = "product_type")
    private Integer productType;

    /**
     * 服务生效时长
     */
    @Column(name = "product_available_day")
    private Integer productAvailableDay;


    /**
     * "CNY"
"USD"
"GBP"

     */
    @Column(name = "product_currency")
    private String productCurrency;


}