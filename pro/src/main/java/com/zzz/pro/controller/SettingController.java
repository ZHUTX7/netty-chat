package com.zzz.pro.controller;

import com.zzz.pro.controller.form.DeleteSettingForm;
import com.zzz.pro.controller.form.SettingTagForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.SettingService;
import com.zzz.pro.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/setting")
public class SettingController {

    @Resource
    private SettingService settingService;

    @GetMapping("/getSetting")
    public SysJSONResult getSetting(@Param("userId") String userId){
        return ResultVOUtil.success(settingService.querySetting(userId));
    }


    //添加用户设置信息
    @PostMapping("/addSetting")
    public SysJSONResult addUserTag(@RequestBody SettingTagForm form){
        //userService.addUserTag(userTag);
        settingService.insertOrUpdateSetting(form);
        return  ResultVOUtil.success();
    }

    //删除用户设置信息
    @PostMapping("/deleteSetting")
    public SysJSONResult deleteUserTag(@RequestBody DeleteSettingForm form){
        settingService.deleteSetting(form);
        return  ResultVOUtil.success();
    }

}
