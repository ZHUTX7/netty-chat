package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.bo.SkuProductBO;
import com.mindset.ameeno.utils.MyMapper;
import com.mindset.ameeno.pojo.dto.AmeenoSkuProduct;

import java.util.List;

public interface AmeenoSkuProductMapper extends MyMapper<AmeenoSkuProduct> {
    public List<SkuProductBO> selectBySkuId(String skuId);
}