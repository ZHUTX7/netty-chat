package com.zzz.pro.service;

import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserRepository userRepository;

    @Override
    public SysJSONResult userLogin(UserBaseInfo userBaseInfo) {
        userRepository.queryUsernameIsExist(userBaseInfo.getUserPhone());
        UserBaseInfo userBaseInfo1 =  userRepository.queryUserInfoByPhone(userBaseInfo.getUserPhone());
        return SysJSONResult.ok(userBaseInfo1);
    }
}
