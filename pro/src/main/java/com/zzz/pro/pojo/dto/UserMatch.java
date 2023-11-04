package com.zzz.pro.pojo.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_match")
@Component
public class UserMatch {
    /**
     * 序列号
     */
    @Id
    private int id;

    @Column(name = "my_user_id")
    private String myUserId;

    @Column(name = "match_user_id")
    private String matchUserId;

    /**
     * 活跃状态
     */
    @Column(name = "active_state")
    private Integer activeState;

    @Column(name = "match_time")
    private Date matchTime;
}