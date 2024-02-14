package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.UserPhoto;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserPhotoMapper extends MyMapper<UserPhoto> {

    //更新用户图片索引
    @Update("update user_photo set photo_index = #{photoIndex} where user_id = #{userId} and photo_id = #{photoId}")
    int updateUserPhotoIndex(String userId,String photoId,Integer photoIndex);

    void updatePhotoIndex(String photoId,Integer photoIndex);

    void deleteByIdIn(List<String> photoIds);

    //查找用户头像图片
    @Select("select photo_name from user_photo where user_id = #{userId} and photo_index = 0")
    String selectFaceImage(String userId);

    @MapKey("user_id")
    List<Map<String, Object>> selectFaceImageIn(List<String> userIdList);

    List<Map<String,Object>> getUserInfoWithPhoto(List<String> userId);

    //根据UserId查询用户所有照片
    @Select("select * from user_photo where user_id = #{userId}")
    List<UserPhoto> queryUserPhotoList(String userId);

}
