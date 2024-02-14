package com.mindset.ameeno.service.api;

import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.utils.MsmConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class SmsService {
    @Resource
    private RedisStringUtil redisStringUtil;

    //发送短信 ,返回验证码
    public String sendSms(String regionCode,String phoneNumber,String countryCode){
        String code =  MsmConstantUtils.generateValidateCode(6);
        if(MsmConstantUtils.sendPhone(regionCode,countryCode,phoneNumber,code)) {
            log.info("短信发送成功，手机号：{}，验证码：{}",phoneNumber,code);
            redisStringUtil.set(phoneNumber,code,120);
            return code;
        }
        else {
            log.info("短信发送失败，手机号：{}，验证码：{}",phoneNumber,code);
            return null;
        }
    }
    //群发短信

}
