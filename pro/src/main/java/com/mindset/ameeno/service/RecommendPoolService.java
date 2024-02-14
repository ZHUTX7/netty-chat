package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.vo.NearPeopleVO;
import com.mindset.ameeno.controller.vo.NearUserVO;
import com.mindset.ameeno.enums.RedisKeyEnum;
import com.mindset.ameeno.pojo.dto.UserGps;
import com.mindset.ameeno.utils.JsonUtils;
import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.utils.RedisUtil;
import com.mindset.ameeno.controller.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RecommendPoolService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RedisStringUtil redisStringUtil;
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

        List<String> superLikeList =  redisStringUtil.lget(RedisKeyEnum.MATCH_SUPER_LIKE_POOL.getCode()+userId,0,3);
        if(!CollectionUtils.isEmpty(superLikeList)){
            for (String e : superLikeList){
                NearUserVO vo = new NearUserVO();
                UserVO userVO = userVOCache.getUserVO(e);
                if(userVO == null){
                    continue;
                }
                BeanUtils.copyProperties(userVO, vo);
                vo.setSuperLike(1);
                vo.setDistance(mapService.getDistance(userId,e));
                result.add(vo);
            }
        }

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
            vo.setSuperLike(0);
            result.add(vo);
            curSize ++;
        }
        nearPeopleVO.setUserList(result);
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
