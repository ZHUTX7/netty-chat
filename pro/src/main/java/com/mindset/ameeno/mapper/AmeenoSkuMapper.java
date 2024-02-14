package com.mindset.ameeno.mapper;

import com.mindset.ameeno.utils.MyMapper;
import com.mindset.ameeno.pojo.dto.AmeenoSku;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AmeenoSkuMapper extends MyMapper<AmeenoSku> {

    @Select("select * from ameeno_sku where sku_sales_status = #{skuSalesStatus}")
    List<AmeenoSku> selectBySkuSalesStatus(String skuSalesStatus);
}