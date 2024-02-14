package com.mindset.ameeno.controller;

import com.mindset.ameeno.controller.form.ReportForm;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.ReportService;
import com.mindset.ameeno.utils.ResultVOUtil;
import com.mindset.ameeno.controller.form.ReportFeedbackForm;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/11 16:23
 * @Version 1.0
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @PostMapping("/submit")
    public SysJSONResult submitReport(@RequestBody ReportForm form){
        reportService.submitReport(form);
        return  ResultVOUtil.success("举报成功~我们将尽快给您反馈！",null);
    }
    @PostMapping("/feedback")
    public SysJSONResult feedbackReport(@RequestBody ReportFeedbackForm form){
        reportService.feedback(form);
        return  ResultVOUtil.success("反馈成功",null);
    }
    @GetMapping("/query")
    public SysJSONResult queryReport(@RequestParam("userId") String userId){
        return  ResultVOUtil.success(reportService.queryReport(userId));
    }
}
