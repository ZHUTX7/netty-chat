package com.mindset.ameeno.pojo.bo;

/**
 * @Author zhutianxiang
 * @Description 高德地图返回值
 * @Date 2023/8/31 00:16
 * @Version 1.0
 */

import com.mindset.ameeno.controller.vo.POI;
import lombok.Data;

/**
 * ApifoxModel
 */
@Data
public class PoiVO {
    private String count;
    private String info;
    private String infocode;
    private POI[] pois;
    private String status;

}