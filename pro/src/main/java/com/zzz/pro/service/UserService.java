package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    SysJSONResult userLogin(UserBaseInfo userBaseInfo);
}
