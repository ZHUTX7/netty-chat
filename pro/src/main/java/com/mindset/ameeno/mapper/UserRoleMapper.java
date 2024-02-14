package com.mindset.ameeno.mapper;


import com.mindset.ameeno.pojo.dto.UserRole;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

public interface UserRoleMapper extends MyMapper<UserRole> {
    //根据用户ID和SKU类型查询用户道具包
    @Select("select * from user_role where user_id = #{userId} limit 1 ")
    UserRole selectByUserId(String userId);

    @Select("SELECT IFNULL((SELECT role_type FROM user_role WHERE user_id = #{userId} LIMIT 1), 'NORMAL') as role_type;")
    String selectRoleTypeByUserId(String userId);
}