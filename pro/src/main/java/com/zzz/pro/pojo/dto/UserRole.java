package com.zzz.pro.pojo.dto;

import javax.persistence.*;
import java.util.Date;

@Table(name = "user_role")
public class UserRole {
    @Id
    @Column(name = "id")
    //自动增长生成ID标签
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    /**
     * 1.normal
2. Vip
3.SVIP
4.SSVIP
     */
    @Column(name = "role_type")
    private Integer roleType;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取1.normal
2. Vip
3.SVIP
4.SSVIP
     *
     * @return role_type - 1.normal
2. Vip
3.SVIP
4.SSVIP
     */
    public Integer getRoleType() {
        return roleType;
    }

    /**
     * 设置1.normal
2. Vip
3.SVIP
4.SSVIP
     *
     * @param roleType 1.normal
2. Vip
3.SVIP
4.SSVIP
     */
    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    /**
     * 获取过期时间
     *
     * @return expire_time - 过期时间
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * 设置过期时间
     *
     * @param expireTime 过期时间
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}