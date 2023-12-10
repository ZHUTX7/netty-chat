package com.zzz.pro.service;

import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.controller.vo.NearPeopleVO;
import com.zzz.pro.controller.vo.NearUserVO;
import com.zzz.pro.controller.vo.UserVO;
import com.zzz.pro.utils.JsonUtils;
import com.zzz.pro.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RecommendPoolService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MapService mapService;
    @Resource
    private UserVOCache userVOCache;
    @Resource
    private BloomFilterService bloomFilterService;

    public NearPeopleVO pullUserList(int count , String userId, Integer distance)  {
        // list分页查询
        List<String> list = mapService.queryNearbyUser(userId,distance);

        int pageSize = 30;
        int pageNum = count;
        int start = pageNum*pageSize;


        //2. UserGps to NearPeopleVO
        List<NearUserVO> result = new ArrayList<>();
        NearPeopleVO nearPeopleVO = new NearPeopleVO();
        if (CollectionUtils.isEmpty(list) || start >= list.size()) {
            return new NearPeopleVO();
        }
        nearPeopleVO.setSum(list.size());
        int curSize = 0;
        for(int i =start ;curSize<pageNum || i< list.size() ;i++){
            String idAndDistance = list.get(i);
            String[] str = idAndDistance.split("-");

            log.info("用户{}匹配到用户{}",userId,str[0]);
            //黑名单筛选
            if(bloomFilterService.mightContain(userId,str[0])) {
                continue;
            }
            //滑过筛选
            Integer isSelected = (Integer)redisUtil.get(RedisKeyEnum.USER_SELECTED_POOL.getCode()+userId+str[0]);
            if(isSelected !=null && isSelected == 1){

                continue;
            }

            NearUserVO vo = new NearUserVO();
            UserVO userVO = userVOCache.getUserVO(str[0]);
            if(userVO == null){
                continue;
            }
            BeanUtils.copyProperties(userVO, vo);
            System.out.println("距离："+ str[1]);
            vo.setDistance(Double.parseDouble(str[1]));
            result.add(vo);
            curSize ++;
        }
        nearPeopleVO.setUserList(result);
        System.out.println(JsonUtils.objectToJson(nearPeopleVO));
        return nearPeopleVO;
    }
    public void addBlackPool(String userId,String targetId){
        bloomFilterService.add(userId,targetId);
    }
    public void addSelectPool(String userId,String targetId){
        redisUtil.set(RedisKeyEnum.USER_SELECTED_POOL.getCode()+userId+targetId,1,6*60);
    }

    public void removeBlackPool(String userId){
        bloomFilterService.remove(userId);
    }
}
