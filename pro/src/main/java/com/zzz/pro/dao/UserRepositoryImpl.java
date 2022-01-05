package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserBaseInfoMapper;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private UserBaseInfoMapper userBaseInfoMapper;

    @Override
    public boolean queryUsernameIsExist(String phone ) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);

        UserBaseInfo result = userBaseInfoMapper.selectOne(user);

        return result != null ? true : false;
    }

    @Override
    public UserBaseInfo queryUserInfoByPhone(String phone) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);

        UserBaseInfo result = userBaseInfoMapper.selectOne(user);

        return result != null ? result : null;
    }
}
