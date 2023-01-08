package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserDatingMapper;
import com.zzz.pro.pojo.dto.UserDating;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDatingRepository {
    @Resource
    private UserDatingMapper userDatingMapper;
    //接受匹配 Status+1 and set UserId
    public void updateDating(UserDating userDating){
        userDatingMapper.updateByPrimaryKey(userDating);
    }
    public UserDating queryDating(UserDating userDating){
        return userDatingMapper.selectOne(userDating);
    }
    public void delDatingData(String datingId){
        UserDating u = new UserDating();
        u.setDatingId(datingId);
        userDatingMapper.deleteByPrimaryKey(u);
    }
}
