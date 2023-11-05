package com.zzz.pro.pojo.dto;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "ameeno_sku")
public class AmeenoSku {
    private Integer id;

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

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return sku_name
     */
    public String getSkuName() {
        return skuName;
    }

    /**
     * @param skuName
     */
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    /**
     * @return sku_desc
     */
    public String getSkuDesc() {
        return skuDesc;
    }

    /**
     * @param skuDesc
     */
    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    /**
     * @return sku_price
     */
    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    /**
     * @param skuPrice
     */
    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    /**
     * @return sku_currency
     */
    public String getSkuCurrency() {
        return skuCurrency;
    }

    /**
     * @param skuCurrency
     */
    public void setSkuCurrency(String skuCurrency) {
        this.skuCurrency = skuCurrency;
    }

    /**
     * @return sku_sales_status
     */
    public String getSkuSalesStatus() {
        return skuSalesStatus;
    }

    /**
     * @param skuSalesStatus
     */
    public void setSkuSalesStatus(String skuSalesStatus) {
        this.skuSalesStatus = skuSalesStatus;
    }
}