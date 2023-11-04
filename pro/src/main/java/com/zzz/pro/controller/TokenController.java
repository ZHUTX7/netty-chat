package com.zzz.pro.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/token")
public class TokenController {
    //token刷新
    @PostMapping("/refresh")
    public void refresh(){
    }

}
