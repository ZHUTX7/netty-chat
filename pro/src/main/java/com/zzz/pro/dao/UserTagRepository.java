package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserTagMapper;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.dto.UserTag;
import com.zzz.pro.pojo.vo.UserTagVO;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Repository
public class UserTagRepository  {
    @Resource
    private UserTagMapper userTagMapper;

    public List<UserTag> queryUserTag(String userId){
        UserTag u = new UserTag();
        u.setUserId(userId);
        return userTagMapper.select(u);
    }

    public void insertUserTag(UserTag u){
        u.setId(UUID.randomUUID().toString());
        userTagMapper.insertUserTag(u);
      //  userTagMapper.insert(u);
    }

    public void updateUserTag(UserTag userTag){
        userTagMapper.updateUserTag(userTag);
    }

    public void delUserTag(String userId,String key){
        UserTag u = new UserTag();
        u.setUserId(userId);
        u.setUserKey(key);
        userTagMapper.delete(u);
    }
}
