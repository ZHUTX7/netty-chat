package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.AmeenoSkuPushRecord;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface AmeenoSkuPushRecordMapper extends MyMapper<AmeenoSkuPushRecord> {
    @Update("UPDATE ameeno_sku_push_record set push_state = #{push_state} ,push_time = #{pushTime} where order_id = {orderId} ")
    int updatePushState(String orderId, int pushState, Date pushTime);

    @Select("SELECT order_id from  ameeno_sku_push_record where order_id in #{orderIds} AND push_state = 0 ")
    List<String> queryUnPushSkuOrderId(List<String> orderIds);
}