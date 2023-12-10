package com.zzz.pro.mapper;

import com.zzz.pro.pojo.dto.AmeenoSku;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AmeenoSkuMapper extends MyMapper<AmeenoSku> {

    @Select("select * from ameeno_sku where sku_sales_status = #{skuSalesStatus}")
    List<AmeenoSku> selectBySkuSalesStatus(String skuSalesStatus);
}