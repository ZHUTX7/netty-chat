package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserBaseInfoMapper;
import com.zzz.pro.mapper.UserMatchMapper;
import com.zzz.pro.mapper.UserPersonalInfoMapper;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class UserRepository {

    @Resource
    private UserMatchMapper userMatchMapper;

    @Resource
    private UserBaseInfoMapper userBaseInfoMapper;
    @Resource
    private UserPersonalInfoMapper userPersonalInfoMapper;

    public boolean queryPhoneIsExist(String phone ) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);
        UserBaseInfo u = userBaseInfoMapper.selectOne(user);
        return u != null ? true : false;
    }

    //查询用户基本信息
    public UserBaseInfo queryUserInfo(String phone,String password) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);
        user.setUserPassword(password);
        UserBaseInfo u = userBaseInfoMapper.selectOne(user);
        return u != null ? u : null;
    }


    //查询用户信息
    @Cacheable(key = "#id",value = "userInfo")
    public UserPersonalInfo queryUserPerInfo(String id) {
        UserPersonalInfo u = new UserPersonalInfo();
        u.setUserId(id);
       UserPersonalInfo userPersonalInfo = userPersonalInfoMapper.selectOne(u);
        return userPersonalInfo != null ? userPersonalInfo : null;
    }


    //添加用户
    public int addUserBaseInfo(UserBaseInfo userBaseInfo) {
        return userBaseInfoMapper.insert(userBaseInfo);

    }


    public int addUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfoMapper.insert(userPersonalInfo);

    }


    public UserMatch queryUserMatch(UserBaseInfo userBaseInfo) {
        UserMatch userMatch = new UserMatch();
        userMatch.setMyUserId(userBaseInfo.getUserId());

        return  userMatchMapper.selectOne(userMatch);
    }


    @CachePut(key = "#id",value = "userInfo")
    public int updateUserProfile(UserPersonalInfo userPersonalInfo) {
        return  userPersonalInfoMapper.updateByPrimaryKey(userPersonalInfo);
    }

    //查询没有匹配的用户 ，目前简单随机匹配，不论男女
    public UserPersonalInfo queryUnMatchUser(UserBaseInfo userBaseInfo) {
        String id = userMatchMapper.queryUnMatchUser(userBaseInfo.getUserId());
        UserPersonalInfo matchUser =new UserPersonalInfo();
        matchUser.setUserId(id);
        matchUser = userPersonalInfoMapper.selectOne(matchUser);
        return matchUser;
    }

    public List<UserPersonalInfo> queryUnMatchUserList(String userId) {
        return userMatchMapper.queryUnMatchUserList(userId);
    }

    public int addMatchUsers(UserMatch userMatch) {
        return userMatchMapper.insert(userMatch);
    }

    //解除匹配
    public int delMatchUsers(UserMatch userMatch) {
        return  userMatchMapper.delete(userMatch);
    }


    public UserPersonalInfo getMatchPerson(String userId) {
        //业务需求 1：1匹配
        List<String> friendList = userMatchMapper.queryMatchUser(userId);
        if(friendList.size()==0){
            return null;
        }
        UserPersonalInfo u = new UserPersonalInfo();
        u.setUserId(friendList.get(0));
        u = userPersonalInfoMapper.selectOne(u);
        return u;
    }


    public void updateUserStatus(UserBaseInfo userBaseInfo) {

        //获得当前时间
        java.util.Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        userBaseInfo.setLastLoginTime(t);
        userBaseInfoMapper.updateByPrimaryKey(userBaseInfo);
    }

    public int delUser(UserBaseInfo userBaseInfo) {
      return userBaseInfoMapper.delete(userBaseInfo);

    }


    public int updateUserFace(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfoMapper.updateByPrimaryKeySelective(userPersonalInfo);
    }

    //根据条件，查询所有用户
    public List<UserPersonalInfo> getAllByExample(UserPersonalInfo userPersonalInfo){
        return userPersonalInfoMapper.selectByExample(userPersonalInfo);
    }
    public List<UserPersonalInfo> getAll(){
        return userPersonalInfoMapper.selectAll();
    }

    //查询所有匹配关系
    public List<UserMatch> getAllMatchedUser(){
        return userMatchMapper.selectAll();
    }

}
