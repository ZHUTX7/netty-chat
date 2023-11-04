package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.controller.form.UpdateProfileForm;
import com.zzz.pro.controller.vo.UserVO;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface UserPersonalInfoMapper extends MyMapper<UserPersonalInfo> {

      @MapKey("user_id")
      List<Map> queryUserOutlineInfo(List<String> userIdList);

      //传入ID List， 查询所有用户的基本信息
      @Select("select user_id,user_nickname from user_personal_info where user_id in (${userIdList})")
      List<Map> queryUserPersonalInfo(List<String> userIdList);

      @MapKey("userId")
      Map<String,Object> queryAllUserVO();
      UserVO queryUserVO(String userId);

      List<UserPersonalInfo> queryAllByUserId(@Param("userIdList") List<String> userIdList);


      //传入ID List， 查询所有用户的基本信息
      @Select("select user_id,user_nickname from user_personal_info where user_id in (${userIdList})")
      List<Map> queryNameById(List<String> userIdList);

      //更新用户信息
        int updateSelectUserPersonal(UpdateProfileForm form);

      //更新用户授权信息
      @Update("update user_personal_info set real_auth = #{userAuth} where user_id = #{userId}")
      int updateUserAuth(@Param("userId") String userId,@Param("userAuth") int userAuth);
}