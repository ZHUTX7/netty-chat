package com.mindset.ameeno.service;

import com.mindset.ameeno.mapper.UserSkuUsedRecordMapper;
import com.mindset.ameeno.pojo.dto.UserPropsBags;
import com.mindset.ameeno.pojo.dto.UserSkuUsedRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description service
 * @Date 2024/2/14 20:08
 * @Version 1.0
 */
@Service
public class SkuUsedRecordService {
    @Resource
    private UserSkuUsedRecordMapper skuUsedRecordMapper;

    public void addRecord(String userId, String targetId, String productId){
        UserSkuUsedRecord record = new UserSkuUsedRecord();
        record.setProductId(productId);

    }
}
