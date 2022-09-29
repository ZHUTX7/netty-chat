package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.result.SysJSONResult;

public interface FriendsService {

    SysJSONResult match(UserBaseInfo userBaseInfo );
    SysJSONResult delMatch(UserMatch userMatch );

    SysJSONResult getMatchPerson(UserBaseInfo userBaseInfo );

}
