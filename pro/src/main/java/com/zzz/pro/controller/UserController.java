package com.zzz.pro.controller;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author ztx
 * @date 2021-12-03 11:27
 * @description : 用户控制器
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/login")
    public SysJSONResult login(@RequestBody UserBaseInfo userBaseInfo){
        //test
        System.out.printf(userBaseInfo.getUserPhone());
        System.out.printf(userBaseInfo.getUserPassword());
        return SysJSONResult.ok("登陆成功");
    }
}
