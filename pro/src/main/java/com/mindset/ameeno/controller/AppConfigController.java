package com.mindset.ameeno.controller;

import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.AppConfigService;
import com.mindset.ameeno.utils.ResultVOUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 
 * @Date 2023/12/10 16:39
 * @Version 1.0
 */
@RestController
@RequestMapping("/app")
public class AppConfigController {
    @Resource
    private AppConfigService configService;
    @GetMapping("/config/query")
    public SysJSONResult queryConfig(){
        return ResultVOUtil.success(configService.getAppConfig());
    }
}
