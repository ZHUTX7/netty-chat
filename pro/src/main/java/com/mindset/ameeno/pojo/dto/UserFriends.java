package com.mindset.ameeno.pojo.dto;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Table(name = "user_friends")
public class UserFriends {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "friends_id")
    private String friendsId;
    @Column(name = "friends_status")
    private Integer friendsStatus;
    @Column(name = "creat_time")
    private Date creatTime;
}
