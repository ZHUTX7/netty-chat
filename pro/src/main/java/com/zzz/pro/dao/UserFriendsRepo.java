package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserFriendsMapper;
import com.zzz.pro.pojo.dto.UserFriends;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFriendsRepo {
    @Resource
    UserFriendsMapper userFriendsMapper;

    public void addFriends(UserFriends u){
        userFriendsMapper.insert(u);
    }

    public void updateFriendsStatus(UserFriends u){
        userFriendsMapper.updateByPrimaryKey(u);
    }

    public void delFriends(UserFriends u){
        userFriendsMapper.delete(u);
    }


    public List<String> queryFriendsId(String userId){
        UserFriends userFriends = new UserFriends();
        userFriends.setUserId(userId);
        Example example=new Example(UserFriends.class);//要查询的表对应的实体类
        Example.Criteria criteria=example.createCriteria();//创建查询标准
        criteria.andEqualTo("userId",userId);//调用方法，编写自己想要查询的条件
        return userFriendsMapper.selectByExample(example).stream().map(e->e.getFriendsId()).collect(Collectors.toList());
    }
}
