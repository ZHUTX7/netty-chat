package com.zzz.pro.service;

import com.zzz.pro.mapper.ReportMapper;
import com.zzz.pro.pojo.dto.Report;
import com.zzz.pro.controller.form.ReportFeedbackForm;
import com.zzz.pro.controller.form.ReportForm;
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
