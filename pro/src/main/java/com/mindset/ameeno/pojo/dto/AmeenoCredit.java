package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "ameeno_credit")
public class AmeenoCredit {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "score")
    private Double score;

    @Column(name = "update_time")
    private Date updateTime;


    @Column(name = "user_phone")
    private String userPhone;


}