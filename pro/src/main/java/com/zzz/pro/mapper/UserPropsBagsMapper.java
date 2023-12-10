package com.zzz.pro.mapper;

import com.zzz.pro.controller.vo.UserSkuVO;
import com.zzz.pro.pojo.dto.UserPropsBags;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserPropsBagsMapper extends MyMapper<UserPropsBags> {
    //根据用户ID和SKU类型查询用户道具包
    @Select("select * from user_props_bags where user_id = #{userId} AND product_id = #{pid}")
    UserPropsBags selectSubscribeSKU(String userId, String pid);


    UserPropsBags selectOneSKU(String userId, String pid);

    //查询我所有的道具

    List<UserSkuVO> selectMyAllSKU(String userId);

    int insertOrUpdateUserPropsBags(UserPropsBags userPropsBags);

}