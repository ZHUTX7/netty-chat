package com.mindset.ameeno.mapper;

import com.mindset.ameeno.pojo.dto.AmeenoCredit;
import com.mindset.ameeno.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface AmeenoCreditMapper extends MyMapper<AmeenoCredit> {

    @Update("update ameeno_credit set score = score - #{score} where  user_id  = #{userId} ")
    void reduceUserScore(String userId,double score);

    @Update("update ameeno_credit set score = score + #{score} where  user_id  = #{userId} ")
    void addUserScore(String userId,double score);

    @Select("select score from ameeno_credit  where  user_id  = #{userId} ")
    double getUserCreditScore(String userId);
}