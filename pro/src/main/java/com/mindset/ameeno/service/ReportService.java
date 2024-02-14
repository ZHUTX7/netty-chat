package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.form.ReportForm;
import com.mindset.ameeno.mapper.ReportMapper;
import com.mindset.ameeno.pojo.dto.Report;
import com.mindset.ameeno.controller.form.ReportFeedbackForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 举报接口
 * @Date 2023/10/11 16:26
 * @Version 1.0
 */
@Slf4j
@Service
public class ReportService {
    @Resource
    private ReportMapper reportMapper;
    public void submitReport(ReportForm form) {
        Report report = new Report();
        BeanUtils.copyProperties(form,report);
        report.setReportTime(new Date());
        report.setReportType(1);
        reportMapper.insert(report);
    }
    public List<Report> queryReport(String userId) {
        return reportMapper.queryReportByUserId(userId);
    }
    public Report feedback(ReportFeedbackForm form) {
        Report report = reportMapper.selectByPrimaryKey(form.getId());
        report.setReportFeedback(form.getReportFeedback());
        reportMapper.updateByPrimaryKey(report);
        return report;
    }

}
