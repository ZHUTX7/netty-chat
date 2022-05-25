package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    SysJSONResult userLogin(UserBaseInfo userBaseInfo);
    SysJSONResult userRegister(UserBaseInfo userBaseInfo);
    SysJSONResult userIsExist(UserBaseInfo userBaseInfo );
    SysJSONResult delUser(UserBaseInfo userBaseInfo );
    SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo );
    SysJSONResult userLoginByToken(String token );
    SysJSONResult updateUserProfile(UserPersonalInfo userPersonalInfo );

    SysJSONResult match(UserBaseInfo userBaseInfo );
    SysJSONResult delMatch(UserMatch userMatch );

    SysJSONResult getMatchPerson(UserBaseInfo userBaseInfo );
}
