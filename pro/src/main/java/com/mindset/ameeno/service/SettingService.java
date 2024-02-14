package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.form.DeleteSettingForm;
import com.mindset.ameeno.controller.form.SettingTagForm;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.mapper.UserSettingMapper;
import com.mindset.ameeno.controller.vo.SettingVO;
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
