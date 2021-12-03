package com.zzz.pro.controller;

import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ztx
 * @date 2021-12-03 11:27
 * @description : 用户控制器
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/login")
    public SysJSONResult login(){
        //test
        return SysJSONResult.errorMsg("用户名或密码不能为空...");
    }
}
