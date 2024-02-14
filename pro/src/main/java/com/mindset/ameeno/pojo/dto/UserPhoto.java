package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_photo")
public class UserPhoto {
    /**
     * 序列号
     */
    @Id
    @Column(name = "photo_id")
    private String photoId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 图片地址
     */
    @Column(name = "photo_url")
    private String photoUrl;

    /**
     * 图片序号
     */
    @Column(name = "photo_index")
    private Integer photoIndex;

    /**
     * 图片创建时间
     */
    @Column(name = "photo_create_time")
    private Date photoCreateTime;

    /**
     * 图片序号
     */
    @Column(name = "photo_name")
    private String photoName;


}
