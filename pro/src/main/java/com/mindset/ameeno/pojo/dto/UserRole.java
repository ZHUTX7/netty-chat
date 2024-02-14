package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "user_role")
public class UserRole {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    /**
     * 1.normal
2. Vip
3.SVIP
4.SSVIP
     */
    @Column(name = "role_type")
    private String roleType;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;


}