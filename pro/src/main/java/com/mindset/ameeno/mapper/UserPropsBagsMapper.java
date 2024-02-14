package com.mindset.ameeno.mapper;

import com.mindset.ameeno.controller.vo.UserSkuVO;
import com.mindset.ameeno.pojo.dto.UserPropsBags;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface UserPropsBagsMapper extends MyMapper<UserPropsBags> {
    //根据用户ID和SKU类型查询用户道具包
    @Select("select * from user_props_bags where user_id = #{userId} AND product_id = #{pid}")
    UserPropsBags selectSubscribeSKU(String userId, String pid);


    UserPropsBags selectOneSKU(String userId, String pid);

    //查询我所有的道具

    List<UserSkuVO> selectMyAllSKU(String userId);

    int insertOrUpdateUserPropsBags(UserPropsBags userPropsBags);

    void updateExpireTime(String userId, String productId, Date expireTime) ;

}