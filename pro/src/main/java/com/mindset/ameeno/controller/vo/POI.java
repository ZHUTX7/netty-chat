package com.mindset.ameeno.controller.vo;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/9/1 03:01
 * @Version 1.0
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class POI {
    //自定义
    private Integer poiStatusCode;
    private String poiStatusMsg;

    private String parent;
    private String address;
    private Business business;
    private String distance;
    private String pcode;
    private String adcode;
    private String pname;
    private String cityname;
    private String type;
    private List<Photo> photos;
    private String typecode;
    private String adname;
    private String citycode;
    private Navi navi;
    private String name;
    private String location;
    private String id;

    // 这里添加所有的getter和setter方法
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Business {
        private String opentime_today;
        private String cost;
        private String keytag;
        private String rating;
        private String business_area;
        private String tel;
        private String rectag;
        private String opentime_week;
        private String tag;

        // 这里添加所有的getter和setter方法
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        private String title;
        private String url;

        // 这里添加所有的getter和setter方法
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Navi {
        private String entr_location;
        private String gridcode;
        private String navi_poiid;
        private String exit_location;
        // 这里添加所有的getter和setter方法
    }
}
