package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.bo.SettingBO;
import com.mindset.ameeno.pojo.dto.UserSetting;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserSettingMapper extends MyMapper<UserSetting> {
    void insertOrUpdateSetting(@Param("userId") String userId, @Param("dataList") List<SettingBO> dataList);
    void deleteByUserIdAndUserKeyIn(String userId,List<String> keys);

    //根据user_id查询user_key,user_value
    @Select("select setting_key,setting_value,setting_tag from user_setting where user_id = #{userId}")
    List<Map> querySettingByUserId(String userId);
}
