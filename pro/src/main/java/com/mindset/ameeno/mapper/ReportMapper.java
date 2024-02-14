package com.mindset.ameeno.mapper;

import com.mindset.ameeno.utils.MyMapper;
import com.mindset.ameeno.pojo.dto.Report;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 举报Mapper
 * @Date 2023/10/11 16:21
 * @Version 1.0
 */
public interface ReportMapper extends MyMapper<Report> {
    //根据userId查询举报
    @Select("select * from report where user_id = #{userId}")
    List<Report> queryReportByUserId(String userId);
}
