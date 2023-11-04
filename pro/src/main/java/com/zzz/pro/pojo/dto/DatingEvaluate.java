package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author zhutianxiang
 * @Description 用户约会评分表
 * @Date 2023/8/24 23:55
 * @Version 1.0
 */

@Data
@Table(name = "dating_evaluate")
public class DatingEvaluate {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "dating_id")
    private String datingId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_target_id")
    private String targetId;
    @Column(name = "score")
    private float score;
    @Column(name = "evaluate")
    private String evaluate;
    @Column(name = "evaluate_time")
    private Date evaluateTime;
}
