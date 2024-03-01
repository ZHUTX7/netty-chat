package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_sku_used_record")
public class UserSkuUsedRecord {
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "match_id")
    private String matchId;

    @Column(name = "use_time")
    private Date useTime;
}