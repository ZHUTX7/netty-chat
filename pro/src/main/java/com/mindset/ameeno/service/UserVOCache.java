package com.mindset.ameeno.service;

import com.alibaba.fastjson.JSONObject;
import com.mindset.ameeno.enums.RedisKeyEnum;
import com.mindset.ameeno.mapper.UserPersonalInfoMapper;
import com.mindset.ameeno.pojo.dto.UserPersonalInfo;
import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.mapper.UserPhotoMapper;
import com.mindset.ameeno.mapper.UserRoleMapper;
import com.mindset.ameeno.controller.vo.UserVO;
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

            // 预览模式，IOS过审
            if(userId.equals("b4debc8e")){
                vo.setUserType("PREVIEW");
            }else {
                vo.setUserType("DEFAULT");
            }

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

    public void deleteUserVO(String userId){
        redisStringUtil.hdel(RedisKeyEnum.ALL_USER_VO.getCode(),userId);
    }

}
