package com.zzz.pro.mapper;

import com.zzz.pro.pojo.dto.AmeenoOrders;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AmeenoOrdersMapper extends MyMapper<AmeenoOrders> {
    @Select("SELECT * FROM ameeno_orders where user_id = #{user_id}")
    List<AmeenoOrders> queryByUserId(String userId);
}