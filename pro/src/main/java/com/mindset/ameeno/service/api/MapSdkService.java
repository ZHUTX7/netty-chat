package com.mindset.ameeno.service.api;

import com.mindset.ameeno.controller.vo.POI;
import com.mindset.ameeno.service.DatingService;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.pojo.bo.PoiVO;
import com.mindset.ameeno.utils.HttpClientUtils;
import com.mindset.ameeno.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Resource
    private DatingService datingService;

    // 1. 附近有营业中且距离合适的地点
    // 2. 附近距离不合适
    // 3. 距离合适未营业
    //获取周边服务
    public POI queryNearbyService(double lat1, double lng1, Integer distance){
        if(distance==null || distance>3 || distance<1){
            distance = 3000;
        }
        else {
            distance = distance*1000;
        }
        String url = "https://restapi.amap.com/v5/place/around?output=json&location="
                +lat1+","+lng1+"&key=" +key
                +"&radius="+distance
                +"&sortrule=weight"
                +"&page_size=25"
                +"&city_limit=true"
                +"&show_fields=business,navi,photos"
                +"&types="+getServiceCode();
       String json =   httpClientUtils.getForObject(url,null,String.class);
       PoiVO poiVO =  JsonUtils.jsonToPojo(json, PoiVO.class);
       if(ObjectUtils.isEmpty(poiVO) || poiVO.getPois().length== 0 ){
           POI poi = new POI();
           poi.setPoiStatusCode(ResultEnum.DATING_POINT_NOT_SUIT.getCode());
           poi.setPoiStatusMsg(ResultEnum.DATING_POINT_NOT_SUIT.getTitle());
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

    public String getServiceCode(){
        String code = "050301|050302|050303|050308|050309|050310|050311|050500|050501|050502|050503|050504|060100|060201|060202|060400|080304|080601|110100|061001";
        return code;
    }

}
