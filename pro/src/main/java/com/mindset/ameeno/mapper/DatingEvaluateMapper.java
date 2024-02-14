package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.DatingEvaluate;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/8/25 00:01
 * @Version 1.0
 */
@Repository
public interface DatingEvaluateMapper extends MyMapper<DatingEvaluate> {
    //根据userId和targetId查询约会评价
    @Select("select * from dating_evaluate where user_id = #{userId} and user_target_id = #{targetId} limit 1")
    DatingEvaluate queryDatingEvaluate(String userId, String targetId);

    @Insert("insert into dating_evaluate(id, dating_id, user_id, user_target_id, score, evaluate, evaluate_time) values(#{id}, #{datingId}, #{userId}, #{targetId}, #{score}, #{evaluate}, #{evaluateTime})")
    void insertDatingEvaluate(DatingEvaluate datingEvaluate);
}
