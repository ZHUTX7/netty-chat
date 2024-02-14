package com.mindset.ameeno.pojo.dto;

import lombok.Data;
import lombok.Generated;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_dating")
public class UserDating {
    @Id
    @Column(name = "dating_id")
    private Integer datingId;
    //0-都未同意 1-仅自己同意 2-仅对方同意  3-全部同意 4-约会完成
    @Column(name = "status")
    private Integer status;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_target_id")
    private String userTargetId;
    @Column(name = "dating_time")
    private Date datingTime;

}
