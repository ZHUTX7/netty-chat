package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "user_props_bags")
public class UserPropsBags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_count")
    private Integer productCount;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 获得时间
     */
    @Column(name = "get_time")
    private Date getTime;

    /**
     * 是否有时间限制  1= 有 0=没有
     */
    @Column(name = "time_limit_flag")
    private int timeLimitFlag;
}