package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.AmeenoOrders;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AmeenoOrdersMapper extends MyMapper<AmeenoOrders> {
    @Select("SELECT * FROM ameeno_orders where user_id = #{user_id}")
    List<AmeenoOrders> queryByUserId(String userId);

    @Select("SELECT * FROM ameeno_orders where user_id = #{user_id} and buy_sku_id >= '1012' ")
    List<AmeenoOrders> queryVipOrdersByUserId(String userId);
}