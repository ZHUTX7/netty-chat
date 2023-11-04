package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
@Repository
public interface UserMatchMapper extends MyMapper<UserMatch> {
     String queryUnMatchUser(String userId);

     List<String> queryMatchUser(String userId);

     List<UserPersonalInfo> queryUnMatchUserList(String userId);

     //查询userId的用户是否有匹配的用户
     @Select("select count(*) from user_match where my_user_id = #{userId} AND active_state = 20 ")
     int queryMatchUserCount(String userId);

     //查询匹配时间
     @Select("select match_time from user_match where my_user_id = #{userId} and match_user_id = #{matchUserId} limit 1")
     Date queryMatchTime(String userId, String matchUserId);

     //根据用户ID查询匹配信息
     @Select("select * from user_match where my_user_id = #{userId} AND active_state != 4 order by match_time desc limit 1 ")
     List<UserMatch> queryMatchInfo(String userId);

     //修改匹配状态
     @Update("update user_match set active_state = #{iState} where my_user_id = #{userId} and match_user_id = #{matchUserId};" +
             "update user_match set active_state = #{uState} where my_user_id = #{matchUserId} and match_user_id = #{userId};")
     void updateBothMatchState(String userId, String matchUserId, Integer iState, Integer uState);

     //修改自己匹配状态
     @Update("update user_match set active_state = #{iState} where my_user_id = #{userId} and match_user_id = #{matchUserId};")
     void updateMyMatchState(String userId, String matchUserId, Integer iState);

     //根据user_id删除匹配关系
     @Delete("DELETE FROM user_match where my_user_id = #{userId} AND match_user_id = #{matchUserId}; "+
             "DELETE FROM user_match where my_user_id = #{matchUserId}  AND match_user_id = #{userId};")
     void deleteRelByUid(String userId, String matchUserId);

     //根据user_id的匹配状态
     @Select("SELECT  active_state FROM user_match where my_user_id = #{userId} and match_user_id = #{matchUserId} limit 1")
     int queryMatchState(String userId,String matchUserId);
}