package com.mindset.ameeno.pojo.dto;

/**
 * @Author zhutianxiang
 * @Description 约会记录表
 * @Date 2023/8/29 14:50
 * @Version 1.0
 */

import lombok.Data;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Entity
@Table(name = "dating_record")
public class DatingRecord {
    @Id
    @Column(name = "dating_record_id", length = 100)
    // 匹配记录ID
    private String datingRecordId;

    @Column(name = "match_time")
    // 匹配时间
    private Timestamp matchTime;

    @Column(name = "dating_time")
    // 约会时间
    private Timestamp datingTime;

    @Column(name = "arrive_time")
    // 到达时间
    private Timestamp arriveTime;

    @Column(name = "complete_time")
    // 完成时间
    private Timestamp completeTime;

    @Column(name = "destination_name", columnDefinition = "VARCHAR(255) DEFAULT 'DESTINATION'")
    // 见面地点
    private String destinationName;

    @Column(name = "location", length = 30)
    // 见面地点经纬度
    private String location;

    @Column(name = "p_name", length = 50)
    // 见面地点省份名称
    private String pName;

    @Column(name = "city_name", length = 30)
    // 见面地点城市名称
    private String cityName;

    @Column(name = "ad_name", length = 30)
    // 见面地点区县名称
    private String adName;

    @Column(name = "address", length = 30)
    // 见面详细地址
    private String address;

    @Column(name = "tel_number", length = 30)
    // 联系电话
    private String telNumber;

    @Column(name = "entr_location", length = 30)
    // 入口经纬度
    private String entrLocation;

    @Column(name = "image_url", length = 255)
    // 图片链接
    private String imageUrl;

    @Column(name = "extra_info", columnDefinition = "TEXT")
    // 更多信息
    private String extraInfo;
}
