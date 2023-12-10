package com.zzz.pro.mapper;

import com.zzz.pro.pojo.bo.SkuProductBO;
import com.zzz.pro.pojo.dto.AmeenoProduct;
import com.zzz.pro.pojo.dto.AmeenoSkuProduct;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AmeenoSkuProductMapper extends MyMapper<AmeenoSkuProduct> {
    public List<SkuProductBO> selectBySkuId(String skuId);
}