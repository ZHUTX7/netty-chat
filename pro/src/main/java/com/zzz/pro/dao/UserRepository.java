package com.zzz.pro.dao;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRepository {
    /**
     * @Description: 判断用户名是否存在
     */
    boolean queryPhoneIsExist(String phone);

    /**
     * @Description: 查询用户基本信息
     */
    UserBaseInfo queryUserInfo(String phone,String password);

    /** TODO
     * @Description: 查询用户个人信息
     */
    UserPersonalInfo queryUserPerInfo(String id);

    /** TODO
     * @Description: 添加用户基础信息
     */
    int addUserBaseInfo(UserBaseInfo userBaseInfo);

    void updateUserStatus(UserBaseInfo userBaseInfo);

    int delUser(UserBaseInfo userBaseInfo);

    int updateUserFace(UserPersonalInfo userPersonalInfo);

    int addUserPersonalInfo(UserPersonalInfo userPersonalInfo);

    UserMatch queryUserMatch(UserBaseInfo userBaseInfo);

    int updateUserProfile(UserPersonalInfo userPersonalInfo);

    UserPersonalInfo queryUnMatchUser(UserBaseInfo userBaseInfo);

    //添加匹配关系
    int addMatchUsers(UserMatch userMatch);

    //删除匹配关系
    int delMatchUsers(UserMatch userMatch);


    UserPersonalInfo getMatchPerson(String  userId);
}
