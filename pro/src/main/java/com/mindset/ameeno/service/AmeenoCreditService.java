package com.mindset.ameeno.service;

import com.mindset.ameeno.mapper.AmeenoCreditMapper;
import com.mindset.ameeno.pojo.dto.AmeenoCredit;
import com.mindset.ameeno.utils.CRCUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @Author zhutianxiang
 * @Description credit manager
 * @Date 2024/2/17 14:14
 * @Version 1.0
 */
@Service
public class AmeenoCreditService {

    @Resource
    private AmeenoCreditMapper creditMapper;

    public void userCreditInit(String userId,String userPhone){
        AmeenoCredit credit  = new AmeenoCredit();
        credit.setUserId(userId);
        credit.setId(CRCUtil.crc32Hex(UUID.randomUUID().toString()));
        credit.setUpdateTime(new Date());
        credit.setUserPhone(userPhone);
        credit.setScore(100.00);
        creditMapper.insert(credit);
    }

    public void userCreditReduce(String userId,double updateScore){
        creditMapper.reduceUserScore(userId,updateScore);
    }

    public void userCreditAdd(String userId,double updateScore){
        creditMapper.reduceUserScore(userId,updateScore);
    }
}
