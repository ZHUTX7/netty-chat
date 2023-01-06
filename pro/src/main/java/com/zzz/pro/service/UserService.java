package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.dto.UserTag;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserTagVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    SysJSONResult userLogin(UserBaseInfo userBaseInfo);
    SysJSONResult userRegister(UserBaseInfo userBaseInfo);
    SysJSONResult userIsExist(UserBaseInfo userBaseInfo );
    SysJSONResult delUser(UserBaseInfo userBaseInfo );
    SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo );
    SysJSONResult userLoginByToken(String token );
    SysJSONResult updateUserProfile(UserPersonalInfo userPersonalInfo );

    //查找未读信息
    SysJSONResult getUnReadMessage(UserBaseInfo userBaseInfo );

    void updateUserTag(UserTag userTag);
    List<UserTagVO> queryUserTag(String userId);
    void addUserTag(UserTag userTag);
    void clearUserTag(UserTag userTag);
}
