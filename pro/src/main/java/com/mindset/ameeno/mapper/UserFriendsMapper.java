package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.UserFriends;
import com.mindset.ameeno.controller.vo.FriendsVO;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserFriendsMapper extends MyMapper<UserFriends> {
//    @Select("select friends_id from user_friends where user_id = #{userId} and friends_status != 2 and friends_status != 4 ; ")
//    List<String> queryFriendsList(String userId,int status);
    @Select("select * from user_friends where user_id = #{userId} and friends_status = 1 ; ")
    List<UserFriends> queryFriendsList(String userId);

    @Update("update user_friends set friends_status = #{status} where user_id = #{userId} and friends_id= #{friendsId} ; ")
    void updateFriendsStatus(String userId,String friendsId,int status);

    @Select("select friends_status from user_friends where user_id = #{userId} and friends_id= #{friendsId}  ; ")
    Integer queryFriendsRelStatus(String userId,String friendsId);

    List<FriendsVO> selectFriendsList(String userId);


}