package com.zzz.pro.mapper;

import com.zzz.pro.pojo.dto.UserDating;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserDatingMapper extends MyMapper<UserDating> {
    @Update("Update user_dating set status = #{status} where user_id=#{userId} and user_targetid =#{targetId}")
    public void updateMyselfStatus(@Param("userId") String userId, @Param("targetId") String targetId, @Param("status")Integer status);

    @Select("Select status from user_dating where dating_id = #{datingId}")
    Integer queryDatingStatus(String datingId);

    @Update("Update user_dating set status = #{iStatus} where user_id=#{userId} and user_targetid =#{targetId};" +
            "Update user_dating set status = #{uStatus} where user_id=#{targetId} and user_targetid =#{userId};")
    public void updateBothDatingStatus(@Param("userId") String userId, @Param("targetId") String targetId,
                             @Param("iStatus")Integer iStatus,
                             @Param("uStatus")Integer uStatus);
}
