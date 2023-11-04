package com.zzz.pro.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGps {
    private String userId;
    //经度
    private Double longitude;
    //纬度
    private Double latitude;
    //城市CODE
    private String countryCode;
    //距离
    private double distance;
    //经纬度所计算的geohash码
    private String geoCode;
}
