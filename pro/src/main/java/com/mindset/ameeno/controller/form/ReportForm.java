package com.mindset.ameeno.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/11 16:29
 * @Version 1.0
 */
@Data
public class ReportForm {

    @NotBlank(message = "举报人id不能为空")
    private String userId;
    @NotBlank(message = "举报对象id不能为空")
    private String targetId;
    private Date reportTime;
    private String reportReason;
    private String userEmail;
    private Integer reportType;
    private String reportFeedback;
    private String reportImage;
}
