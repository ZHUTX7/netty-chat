package com.mindset.ameeno.mapper;


import com.mindset.ameeno.pojo.dto.UserBaseInfo;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBaseInfoMapper extends MyMapper<UserBaseInfo> {
    @Select("select * from user_base_info where user_phone = #{phone} limit 1")
    UserBaseInfo selectByPhone(String phone);


    //更新user_role
    @Select("update user_base_info set user_role = #{userRole} where user_id = #{userId}")
    void updateUserRole(String userId, String userRole);


    @Update("update user_base_info set user_phone = #{newPhone} where user_id = #{userId}")
    void updateUserPhoneByUserId(String userId, String newPhone);

    //验证手机号是否存在
    @Select("select count(*) from user_base_info where user_phone = #{phone}")
    int checkPhone(String phone);
}