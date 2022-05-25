package com.zzz.pro.pojo.InterfaceDto;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import org.springframework.stereotype.Component;

@Component
public class LoginDTO {
    private String loginMethod;
    private UserBaseInfo loginParams;

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public UserBaseInfo getLoginParams() {
        return loginParams;
    }

    public void setLoginParams(UserBaseInfo loginParams) {
        this.loginParams = loginParams;
    }
}
