package com.zzz.pro.service;

import com.alibaba.fastjson.JSONObject;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.mapper.UserPersonalInfoMapper;
import com.zzz.pro.mapper.UserPhotoMapper;
import com.zzz.pro.mapper.UserRoleMapper;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.controller.vo.UserVO;
import com.zzz.pro.pojo.dto.UserRole;
import com.zzz.pro.utils.RedisStringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class UserVOCache {
    @Resource
    private RedisStringUtil redisStringUtil;
    @Resource
    private UserPersonalInfoMapper personalInfoMapper;
    @Resource
    private UserPhotoMapper userPhotoMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    public UserVO getUserVO(String userId) {
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        String  json  = (String) redisStringUtil.hget(RedisKeyEnum.ALL_USER_VO.getCode(),userId);
        UserVO  vo = null;
        if(StringUtils.isEmpty(json)){
            //从数据库中获取
            UserPersonalInfo personalInfo = personalInfoMapper.selectByPrimaryKey(userId);
            if(personalInfo == null){
                return null;
            }
            vo = new UserVO();
            vo.setRealAuth(personalInfo.getRealAuth());
            vo.setUserId(personalInfo.getUserId());
            vo.setUserNickName(personalInfo.getUserNickname());
            vo.setUserSex(personalInfo.getUserSex());
            vo.setUserHometown(personalInfo.getUserHometown());
            vo.setBirthDate(personalInfo.getUserBirthdate());
            vo.setUserRole( userRoleMapper.selectRoleTypeByUserId(userId));
            String facePhoto =  userPhotoMapper.selectFaceImage(userId);
            facePhoto = facePhoto ==null ? "":facePhoto;
            vo.setUserImage(facePhoto);
            //放入缓存
            saveUserVO(vo);
            return vo;
        }
        vo = JSONObject.parseObject(json,UserVO.class);
        return  vo ;
    }
    public void saveUserVO(UserVO vo){
        if(vo == null){
            return;
        }
        redisStringUtil.hset(RedisKeyEnum.ALL_USER_VO.getCode(), vo.getUserId(), JSONObject.toJSONString(vo));
    }

}
