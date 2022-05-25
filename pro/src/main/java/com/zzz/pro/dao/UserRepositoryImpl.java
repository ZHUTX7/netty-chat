package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserBaseInfoMapper;
import com.zzz.pro.mapper.UserMatchMapper;
import com.zzz.pro.mapper.UserPersonalInfoMapper;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private UserMatchMapper userMatchMapper;

    @Resource
    private UserBaseInfoMapper userBaseInfoMapper;
    @Resource
    private UserPersonalInfoMapper userPersonalInfoMapper;

    @Override
    public boolean queryPhoneIsExist(String phone ) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);
        UserBaseInfo u = userBaseInfoMapper.selectOne(user);
        System.out.printf("-----------");
        System.out.println(u);
        return u != null ? true : false;
    }

    @Override
    public UserBaseInfo queryUserInfo(String phone,String password) {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserPhone(phone);
        user.setUserPassword(password);
        UserBaseInfo u = userBaseInfoMapper.selectOne(user);
        return u != null ? u : null;
    }

    @Override
    public UserPersonalInfo queryUserPerInfo(String id) {
        UserPersonalInfo u = new UserPersonalInfo();
        u.setUserId(id);
       UserPersonalInfo userPersonalInfo = userPersonalInfoMapper.selectOne(u);
        return userPersonalInfo != null ? userPersonalInfo : null;
    }

    @Override
    public int addUserBaseInfo(UserBaseInfo userBaseInfo) {
        return userBaseInfoMapper.insert(userBaseInfo);

    }

    @Override
    public int addUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfoMapper.insert(userPersonalInfo);

    }

    @Override
    public UserMatch queryUserMatch(UserBaseInfo userBaseInfo) {
        UserMatch userMatch = new UserMatch();
        userMatch.setMyUserId(userBaseInfo.getUserId());

        return  userMatchMapper.selectOne(userMatch);
    }

    @Override
    public int updateUserProfile(UserPersonalInfo userPersonalInfo) {
        return  userPersonalInfoMapper.updateByPrimaryKey(userPersonalInfo);
    }

    //查询没有匹配的用户 ，目前简单随机匹配，不论男女
    @Override
    public UserPersonalInfo queryUnMatchUser(UserBaseInfo userBaseInfo) {
        String id = userMatchMapper.queryUnMatchUser(userBaseInfo.getUserId());
        UserPersonalInfo matchUser =new UserPersonalInfo();
        matchUser.setUserId(id);
        matchUser = userPersonalInfoMapper.selectOne(matchUser);
        return matchUser;
    }

    @Override
    public int addMatchUsers(UserMatch userMatch) {
        return userMatchMapper.insert(userMatch);
    }

    //解除匹配
    @Override
    public int delMatchUsers(UserMatch userMatch) {
        return  userMatchMapper.delete(userMatch);
    }

    @Override
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

    @Override
    public void updateUserStatus(UserBaseInfo userBaseInfo) {

        //获得当前时间
        java.util.Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        userBaseInfo.setLastLoginTime(t);
        userBaseInfoMapper.updateByPrimaryKey(userBaseInfo);
    }

    @Override
    public int delUser(UserBaseInfo userBaseInfo) {

      return userBaseInfoMapper.delete(userBaseInfo);

    }

    @Override
    public int updateUserFace(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfoMapper.updateByPrimaryKeySelective(userPersonalInfo);
    }



}
