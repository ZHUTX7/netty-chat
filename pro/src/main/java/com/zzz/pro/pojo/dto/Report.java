package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/11 16:12
 * @Version 1.0
 */
@Data
@Table(name = "ameeno_report")
public class Report {
    @Id
    @Column(name = "id")
    private Integer id;
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "target_id")
    private String targetId;
    @Column(name = "report_time")
    private Date reportTime;
    @Column(name = "report_reason")
    private String reportReason;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "report_type")
    private Integer reportType;
    @Column(name = "report_feedback")
    private String reportFeedback;
    @Column(name = "report_image")
    private String reportImage;
}