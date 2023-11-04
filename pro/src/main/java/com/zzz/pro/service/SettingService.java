package com.zzz.pro.service;

import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.UserSettingMapper;
import com.zzz.pro.controller.form.DeleteSettingForm;
import com.zzz.pro.controller.form.SettingTagForm;
import com.zzz.pro.controller.vo.SettingVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService {

    @Resource
    private UserSettingMapper settingMapper;

    @Transactional
    public void deleteSetting(DeleteSettingForm form) {
        settingMapper.deleteByUserIdAndUserKeyIn(form.getUserId(), form.getKeys());
    }

    @Transactional
    public void insertOrUpdateSetting(SettingTagForm form) {
        try{
            settingMapper.insertOrUpdateSetting(form.getUserId(), form.getDataList());
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"数据错误");
        }
    }
    public List<SettingVO> querySetting(String userId) {
        List<Map> list =  settingMapper.querySettingByUserId(userId);

        if(list == null || list.size() == 0){
            return null;
        }

        return  list.stream().map(e->{
            SettingVO vo = new SettingVO();
            vo.setSettingKey(e.get("setting_key").toString());
            vo.setSettingValue(e.get("setting_value").toString());
            vo.setSettingTag(e.get("setting_tag").toString());
            return vo;
        }).collect(Collectors.toList());
    }
}
