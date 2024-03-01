package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.vo.AppConfigVO;
import com.mindset.ameeno.mapper.AppConfigMapper;
import com.mindset.ameeno.pojo.dto.AppConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description
 * @Date 2023/12/10 16:40
 * @Version 1.0
 */
@Service
public class AppConfigService {
    @Resource
    private AppConfigMapper appConfigMapper;
    public AppConfigVO getAppConfig(){
        AppConfig appConfig = new AppConfig();
        AppConfigVO vo = new AppConfigVO();
        appConfig.setId("id-ameeno");
        List<AppConfig> list = appConfigMapper.select(appConfig);
        if(CollectionUtils.isEmpty(list)){
            appConfig.setId("id-ameeno");
            appConfig.setAppName("ameeno");
            appConfig.setPayMode("close");
            appConfig.setAppEnv("dev");
            appConfigMapper.insert(appConfig);
            BeanUtils.copyProperties(appConfig,vo);
            return vo;
        }

        BeanUtils.copyProperties(list.get(0),vo);
        return vo;
    }
}
