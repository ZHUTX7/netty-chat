package com.zzz.pro.pojo.vo;



import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RegisterVO {

    private String userPhone;
    private String verifyCode;
    private String password;
}
