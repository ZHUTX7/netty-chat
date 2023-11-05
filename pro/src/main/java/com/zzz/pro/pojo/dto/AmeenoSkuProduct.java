package com.zzz.pro.pojo.dto;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "ameeno_sku_product")
public class AmeenoSkuProduct {
    private Integer id;

    @Column(name = "sku_id")
    private Integer skuId;

    @Column(name = "product_id")
    private Integer productId;

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
     * @return sku_id
     */
    public Integer getSkuId() {
        return skuId;
    }

    /**
     * @param skuId
     */
    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    /**
     * @return product_id
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * @param productId
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}