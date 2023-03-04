package com.zzz.pro.pojo.InterfaceDto;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginDTO {
    private String loginMethod;
    private UserBaseInfo loginParams;
    private String deviceId;
 }
