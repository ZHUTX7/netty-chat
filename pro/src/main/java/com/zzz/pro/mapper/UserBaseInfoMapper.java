package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBaseInfoMapper extends MyMapper<UserBaseInfo> {
    @Select("select * from user_base_info where user_phone = #{phone} limit 1")
    UserBaseInfo selectByPhone(String phone);

}