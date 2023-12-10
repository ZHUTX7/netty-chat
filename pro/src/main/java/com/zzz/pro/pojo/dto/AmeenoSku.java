package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Table(name = "ameeno_sku")
public class AmeenoSku {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="UUID")
    private String id;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_desc")
    private String skuDesc;

    @Column(name = "sku_price")
    private BigDecimal skuPrice;

    @Column(name = "sku_currency")
    private String skuCurrency;

    @Column(name = "sku_sales_status")
    private String skuSalesStatus;

}