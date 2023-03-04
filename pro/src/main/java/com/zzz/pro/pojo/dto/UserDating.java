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
    private String datingId;
    //0-都未同意 1-仅自己同意 2-仅对方同意  3-全部同意 4-约会完成
    @Column(name = "status")
    private Integer status;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_targetid")
    private String userTargetId;
}
