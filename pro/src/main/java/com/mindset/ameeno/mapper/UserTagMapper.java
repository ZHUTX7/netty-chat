package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.bo.UserTagBO;
import com.mindset.ameeno.pojo.dto.UserTag;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserTagMapper extends MyMapper<UserTag> {

    void insertOrUpdateTag(@Param("userId") String userId,@Param("dataList") List<UserTagBO> userTag);
    void deleteByUserIdAndUserKeyIn(String userId,List<String> keys);

    //根据user_id查询user_key,user_value
    @Select("select user_key,user_value,tag from user_tag where user_id = #{userId}")
    List<Map> queryTagByUserId(String userId);
}
