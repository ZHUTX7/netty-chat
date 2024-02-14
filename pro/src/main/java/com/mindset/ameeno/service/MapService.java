package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.vo.POI;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.service.api.MapSdkService;
import com.mindset.ameeno.utils.CRCUtil;
import com.mindset.ameeno.utils.RedisUtil;
import com.mindset.ameeno.utils.GeoUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 地图API
 * @Date 2023/8/2 23:39
 * @Version 1.0
 */
@Service
@Slf4j
public class MapService {

    @Resource
    private RedisUtil redisUtil;


    //GEO相关命令用到的KEY
    private final static String KEY = "user_point";

    @Resource
    UserVOCache userVOCache;

    @Resource
    MapSdkService mapSdkService;
    @Resource
    DatingService datingService;

    //推荐见面地点
    public POI recommendLocale(String userId, String targetId, Integer distance) {
        double[] point = calculateUserCenter(userId, targetId);
        //1. 先从Redis中取
        String poiId =  CRCUtil.crc32HexBy2Id(userId,targetId);
        POI poi = (POI) redisUtil.get(poiId);
        if(poi != null){
            return poi;
        }
        poi = mapSdkService.queryNearbyService(point[0], point[1], distance);
        if(poi.getPoiStatusCode().equals(ResultEnum.DATING_POINT_NOT_SUIT.getCode())){
            datingService.datingSkip(userId,targetId);
        }
        redisUtil.set(poiId,poi);
        return poi;
    }
    public List<String> queryNearbyUser(String userId, Integer distance) {
        //1. 查看redis中是否有缓存
        List<String> list = new ArrayList<>();
//        List<String> list = redisUtil.lGet(RedisKeyEnum.USER_UN_MATCH_LIST.getCode()+userId);
        if(CollectionUtils.isEmpty(list)){
            return nearBySearchByUserId(userId, distance);
        }
        return list;

    }

    //计算用户的中心位置
    public double[] calculateUserCenter(String userId, String targetId) {
        double[] midPoint = getMidPoint(userId, targetId);
        if (midPoint == null || midPoint.length < 2) {
            return null;
        }
        return midPoint;
    }

    public boolean save(double[] gps, String userId) {

        if(gps == null || gps.length < 2 || StringUtils.isEmpty(userId)){
            log.warn("保存用户位置失败，用户id：{}",userId);
            return false;
        }
        Long flag = redisUtil.getRedisTemplate().opsForGeo().add(KEY, new RedisGeoCommands.GeoLocation<>(
                userId,
                new Point(gps[0],gps[1]))
        );
        boolean result = flag != null && flag > 0;
        return result;
    }

    //计算两个人的距离
    public double getDistance(String userId,String target){
        Distance distance = redisUtil.getRedisTemplate().opsForGeo().distance(KEY, userId, target, Metrics.NEUTRAL);
        if(distance == null){
            log.warn("计算两个人的距离失败，用户id：{}，{}",userId,target);
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"计算两个人的距离失败");
        }
        return distance.getValue();
    }

    /**
     * 获取两个用户的中间位置
     */
    public double[] getMidPoint(String userId1 ,String userId2){
        List<Point> list = redisUtil.getRedisTemplate().opsForGeo().position(KEY, userId1,userId2);
        if(list == null ){
            log.warn("获取两个用户的中间位置失败，用户id：{}，{}",userId1,userId2);
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"获取两个用户的中间位置失败");
        }
        if(list.size() < 2){
            //返回其中一个人的中间位置
            double[] result = new double[2];
            result[0] = list.get(0).getY();
            result[1] = list.get(0).getY();
            return result;
        }
        return  GeoUtils.calculateMidPoint(list.get(0).getX(),list.get(0).getY(),list.get(1).getX(),list.get(1).getY());
    }

    //获取用户附近的人
    public List<String> nearBySearchByUserId(String userId,double distance){
        List<Point> list = redisUtil.getRedisTemplate().opsForGeo().position(KEY, userId);
        if(CollectionUtils.isEmpty(list) || list.get(0) == null){
            log.error("未获取到用户的位置信息，用户id：{}",userId);
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"未获取到用户的位置信息");
        }
        return searchNearbyName(distance,userId);
//        return nearBySearch(userId,distance,list.get(0).getX(),list.get(0).getY());
    }



    /**
     * 查询指定用户，范围内的用户
     */
    public List<String> searchNearbyName(double distance, String userId) {
        List<String> users = new ArrayList<>();
        List<Point> position = redisUtil.getRedisTemplate().opsForGeo().position(KEY, userId);
        Distance distanceArg = new Distance(distance, Metrics.KILOMETERS);
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending();
        // 1.GEORADIUS获取附近范围内的信息
        GeoResults<RedisGeoCommands.GeoLocation<Object>> reslut =
                redisUtil.getRedisTemplate().opsForGeo().radius(KEY, new Circle(position.get(0), distanceArg), geoRadiusArgs);
        //2.收集信息，存入list
        List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> content = reslut.getContent();
        //3.返回计算后的信息
        content.forEach(a -> {
            if(!userId.equals(a.getContent().getName())) {
                double num = Math.floor(a.getDistance().getValue()/1000);
                users.add(a.getContent().getName()+"-"+num);
            }
        });
        return users;
    }
}
