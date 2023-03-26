package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface UserMatchMapper extends MyMapper<UserMatch> {
     String queryUnMatchUser(String userId);

     List<String> queryMatchUser(String userId);

     List<UserPersonalInfo> queryUnMatchUserList(String userId);

     //查询userId的用户是否有匹配的用户
     @Select("select count(*) from user_match where my_user_id = #{userId} ")
     int queryMatchUserCount(String userId);
}