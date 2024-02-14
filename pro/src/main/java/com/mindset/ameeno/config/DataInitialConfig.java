package com.mindset.ameeno.config;


import com.mindset.ameeno.mapper.UserPersonalInfoMapper;
import com.mindset.ameeno.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


//数据预热
@Slf4j
@Configuration
public class DataInitialConfig {
    @Resource
    private RedisStringUtil redisStringUtil;
    @Resource
    private UserPersonalInfoMapper upMapper;
    @Bean
    public void UserDataPoolInit(){
        log.info("redis 用户数据预热开始>>>>>>>>>>>>>>>");
//        Map<String,Object> userVOList = upMapper.queryAllUserVO();
//        redisUtil.hmset(RedisKeyEnum.ALL_USER_VO.getCode(),userVOList);
//        Map<String,UserProfileVO> boys = new HashMap<>();
//        Map<String,UserProfileVO> girls = new HashMap<>();
//        //查询所有用户信息
//        List<UserPersonalInfo> list = userRepository.getAll();
//        if(list.size()==0){
//            return;
//        }
//        //分类
//        for (UserPersonalInfo e: list) {
//            if(e.getUserSex()==1){
//                UserProfileVO userProfileVO = new UserProfileVO();
//                BeanUtils.copyProperties(e,userProfileVO);
//                boys.put(userProfileVO.getUserId(), userProfileVO);
//                log.info("Redis插入数据,User ID : {}",userProfileVO.getUserId());
//            }else {
//                UserProfileVO userProfileVO = new UserProfileVO();
//                BeanUtils.copyProperties(e,userProfileVO);
//                girls.put(userProfileVO.getUserId(), userProfileVO);
//                log.info("Redis插入数据,User ID : {}",userProfileVO.getUserId());
//            }
//        }
//
//        redisTemplate.opsForHash().putAll(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),boys);
//        redisTemplate.opsForHash().putAll(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),girls);
        log.info("redis 用户数据预热结束---------------");
    }
}
