package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.UserDating;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserDatingMapper extends MyMapper<UserDating> {
    @Update("Update user_dating set status = #{status} where user_id=#{userId} and user_target_id =#{targetId}")
    public void updateMyselfStatus(@Param("userId") String userId, @Param("targetId") String targetId, @Param("status")Integer status);

    @Delete("Delete from user_dating where user_id=#{userId} and user_target_id =#{targetId};" +
            "Delete from user_dating where user_id=#{targetId} and user_target_id =#{userId};")
    public void deleteBothDating(@Param("userId") String userId, @Param("targetId") String targetId);

    @Select("Select status from user_dating where user_id=#{userId} and user_target_id =#{targetId}")
    Integer queryDatingStatus(@Param("userId") String userId, @Param("targetId") String targetId);

    @Update("Update user_dating set status = #{iStatus} where user_id=#{userId} and user_target_id =#{targetId};" +
            "Update user_dating set status = #{uStatus} where user_id=#{targetId} and user_target_id =#{userId};")
    public void updateBothDatingStatus(@Param("userId") String userId, @Param("targetId") String targetId,
                             @Param("iStatus")Integer iStatus,
                             @Param("uStatus")Integer uStatus);
    @Update("Update user_dating set status = #{bstatus} ,dating_time = #{time} where user_id=#{userId} and user_target_id =#{targetId} ;" +
            "Update user_dating set status = #{bstatus} ,dating_time = #{time} where user_id=#{targetId} and user_target_id =#{userId} ;")
    public void updateStatusAndTime(@Param("userId") String userId, @Param("targetId") String targetId,
                                    @Param("bstatus")Integer bstatus,
                                    @Param("time") Date time);

    @Update("Update user_dating set status = #{iStatus} where user_id=#{userId};")
    void updateMyStatus(@Param("userId") String userId, @Param("iStatus") Integer iStatus);

    @Select("Select * from user_dating where user_id=#{userId} and user_target_id =#{targetId}")
    UserDating queryDating(@Param("userId") String userId, @Param("targetId") String targetId);

    @Select("Select * from user_dating where user_id=#{userId} and user_target_id =#{targetId} and status != 4")
    UserDating queryInDating(@Param("userId") String userId, @Param("targetId") String targetId);
}
