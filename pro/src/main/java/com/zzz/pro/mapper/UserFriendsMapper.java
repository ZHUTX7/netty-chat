package com.zzz.pro.mapper;

import com.zzz.pro.pojo.dto.UserFriends;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserFriendsMapper extends MyMapper<UserFriends> {
    @Select("select friends_id from user_friends where user_id = #{userId} and friends_status != 2 and friends_status != 4 ; ")
    List<String> queryFriendsList(String userId,int status);

    @Update("update user_friends set friends_status != #{status} where user_id = #{userId} and friends_id= #{friendsId} ; ")
    void updateFriendsStatus(String userId,String friendsId,int status);

    @Select("select friends_status from user_friends where user_id = #{userId} and friends_id= #{friendsId}  ; ")
    Integer queryFriendsRelStatus(String userId,String friendsId);
}