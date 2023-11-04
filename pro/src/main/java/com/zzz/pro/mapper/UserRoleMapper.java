package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserRole;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

public interface UserRoleMapper extends MyMapper<UserRole> {
    //根据用户ID和SKU类型查询用户道具包
    @Select("select * from user_role where user_id = #{userId} limit 1 ")
    UserRole selectByUserId(String userId);
}