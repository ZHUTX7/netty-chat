package com.zzz.pro.pojo.form;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginForm {
    private String loginMethod;
    private String userPhone;
    private String verifyCode;
    private String deviceId;
    private String countryCode;
    private String regionCode;
 }
