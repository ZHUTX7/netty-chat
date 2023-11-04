package com.zzz.pro.service.api;

import com.zzz.pro.pojo.bo.PoiVO;
import com.zzz.pro.controller.vo.POI;
import com.zzz.pro.utils.HttpClientUtils;
import com.zzz.pro.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author zhutianxiang
 * @Description 高德地图
 * @Date 2023/8/3 00:53
 * @Version 1.0
 */
@Service
public class MapSdkService {
    @Value("${amap.key}")
    private String key;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private HttpClientUtils httpClientUtils;

    //获取周边服务
    public POI queryNearbyService(double lat1, double lng1,Integer distance){
        if(distance==null || distance>3 || distance<1){
            distance = 3000;
        }
        else {
            distance = distance*1000;
        }
        //050000 餐饮服务 060200 便利店 080304 酒吧
        //show_fields
        // https://restapi.amap.com/v5/place/around?parameters
        String url = "https://restapi.amap.com/v5/place/around?output=json&location="
                +lat1+","+lng1+"&key=" +key
                +"&radius="+distance
                +"&sortrule=weight"
                +"&page_size=25"
                +"&city_limit=true"
                +"&show_fields=business,navi,photos"
                +"&types=050300|050500|050600|050700|050800|050900|060100|060200|140100|140200|140400|140500";
       String json =   httpClientUtils.getForObject(url,null,String.class);
       PoiVO poiVO =  JsonUtils.jsonToPojo(json, PoiVO.class);
       if(ObjectUtils.isEmpty(poiVO) || poiVO.getPois().length== 0 ){
           POI poi = new POI();
           poi.setPoiStatusCode(500);
           poi.setPoiStatusMsg("附近没有可以见面的地点");
           return poi;
       }
       POI[] poi =  poiVO.getPois();
       POI poiBak = poi[0];
       for(POI e : poi){
           // "opentime_today": "10:00-22:00",
         String openTime =   e.getBusiness().getOpentime_today();
         if(!StringUtils.isEmpty(openTime) && isCurrentTimeInTimeRange(openTime)){
             return  e;
         }
       }
       poiBak.setPoiStatusCode(501);
       poiBak.setPoiStatusMsg("附近没有营业中的商家，随机推荐约会地点,请注意营业时间和安全~");
       return poiBak;
    }

    public static boolean isCurrentTimeInTimeRange(String timeRange) {
        try {
            // 分隔时间段字符串
            String[] parts = timeRange.split("-");
            // 获取当前时间
            Date currentTime = new Date();

            // 创建时间格式化对象，用于将字符串转换为时间
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            // 解析开始时间和结束时间
            Date startTime = sdf.parse(parts[0]);
            Date endTime = sdf.parse(parts[1]);

            // 比较当前时间是否在时间段内
            return currentTime.after(startTime) && currentTime.before(endTime);
        } catch (Exception e) {
            return false;
        }
    }
}
