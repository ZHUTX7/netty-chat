package com.zzz.pro.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "user_dating")
public class UserDating {
    @Id
    @Column(name = "dating_id")
    public String datingId;
    // 1-仅自己同意 2-仅对方同意  3-约会完成
    @Column(name = "status")
    public String status;
    @Column(name = "user_id")
    public String userId;
    @Column(name = "user_targetid")
    public String userTargetId;
}
