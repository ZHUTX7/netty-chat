package com.zzz.pro.dao;

import com.zzz.pro.enums.FriendsStatusEnum;
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

    public void updateFriendsStatus(String userId,String friendsId){
        Integer status =  userFriendsMapper.queryFriendsRelStatus(userId,friendsId);
        userFriendsMapper.updateFriendsStatus(userId,friendsId,status+1);
    }

    public void delFriends(String userId,String friendsId){
        UserFriends u = new UserFriends();
        u.setFriendsId(friendsId);
        u.setUserId(userId);
        userFriendsMapper.delete(u);
    }


    public List<String> queryFriendsId(String userId){
//        UserFriends userFriends = new UserFriends();
//        userFriends.setUserId(userId);
//        Example example=new Example(UserFriends.class);//要查询的表对应的实体类
//        Example.Criteria criteria=example.createCriteria();//创建查询标准
//        criteria.andEqualTo("userId",userId);//调用方法，编写自己想要查询的条件
//        criteria.andNotEqualTo("friendsStatus", FriendsStatusEnum.BOTH_DEL_FRIENDS.getCode());
//        return userFriendsMapper.selectByExample(example).stream().map(e->e.getFriendsId()).collect(Collectors.toList());
        return userFriendsMapper.queryFriendsList(userId,(Integer) FriendsStatusEnum.BOTH_DEL_FRIENDS.getCode());
    }
}
