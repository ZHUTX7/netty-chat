package com.zzz.pro.config;

import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.mapper.UserPersonalInfoMapper;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.vo.UserProfileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//数据预热
@Slf4j
@Configuration
public class DataInitialConfig {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserRepository userRepository;

    @Bean
    public void UserDataPoolInit(){
        log.info("redis 用户数据预热开始>>>>>>>>>>>>>>>");
        Map<String,UserProfileVO> boys = new HashMap<>();
        Map<String,UserProfileVO> girls = new HashMap<>();
        //查询所有用户信息
        List<UserPersonalInfo> list = userRepository.getAll();
        if(list.size()==0){
            return;
        }
        //分类
        for (UserPersonalInfo e: list) {
            if(e.getUserSex()==1){
                UserProfileVO userProfileVO = new UserProfileVO();
                BeanUtils.copyProperties(e,userProfileVO);
                boys.put(userProfileVO.getUserId(), userProfileVO);
                log.info("Redis插入数据,User ID : {}",userProfileVO.getUserId());
            }else {
                UserProfileVO userProfileVO = new UserProfileVO();
                BeanUtils.copyProperties(e,userProfileVO);
                girls.put(userProfileVO.getUserId(), userProfileVO);
                log.info("Redis插入数据,User ID : {}",userProfileVO.getUserId());
            }
        }
        redisTemplate.opsForHash().putAll(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),boys);
        redisTemplate.opsForHash().putAll(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),boys);
        log.info("redis 用户数据预热结束---------------");
    }
}
