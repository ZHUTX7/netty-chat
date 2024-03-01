package com.mindset.ameeno.service;

import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.mapper.AmeenoSkuPushRecordMapper;
import com.mindset.ameeno.pojo.dto.AmeenoSkuPushRecord;
import com.mindset.ameeno.utils.CRCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2024/2/27 19:28
 * @Version 1.0
 */
@Slf4j
@Service
public class SkuPushRecordService {
    @Resource
    AmeenoSkuPushRecordMapper skuPushRecordMapper;

    public void addPushRecord(String orderId,String userId,String receipt){
        AmeenoSkuPushRecord record = new AmeenoSkuPushRecord();
        record.setId(CRCUtil.crc32Hex(UUID.randomUUID().toString()));
        record.setPushState(0);
        record.setPushTime(new Date());
        record.setUserId(userId);
        record.setReceipt(receipt);
        record.setOrderId(orderId);
        skuPushRecordMapper.insert(record);
    }

    @Transactional
    public void skuPushFinished(String oderId){
        int result = skuPushRecordMapper.updatePushState(oderId,1,new Date());
        if(result == 1) {
           log.info("发货成功，发货状态已更新");
        }else {
            log.error("发货更新失败");
            throw new ApiException(500,"发货状态更新失败");
        }
    }

    public List<String> queryUnPushOrder(List<String> orderIds){
        return   skuPushRecordMapper.queryUnPushSkuOrderId(orderIds);
    }
}
