package com.zzz.pro.service.api;

import com.zzz.pro.pojo.bo.SmsBO;
import com.zzz.pro.utils.MsmConstantUtils;
import com.zzz.pro.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class SmsService {
    @Resource
    private RedisUtil redisUtil;

    //发送短信 ,返回验证码
    public String sendSms(String phoneNumber){
        String code =  MsmConstantUtils.generateValidateCode(6);
        if(MsmConstantUtils.sendPhone(phoneNumber,code)) {
            log.info("短信发送成功，手机号：{}，验证码：{}",phoneNumber,code);
            redisUtil.set(phoneNumber,code,120);
            return code;
        }
        else {
            log.info("短信发送失败，手机号：{}，验证码：{}",phoneNumber,code);
            return null;
        }
    }
    //群发短信

}
