package com.zzz.pro.dao;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import org.springframework.stereotype.Service;

@Service
public interface UserRepository {
    /**
     * @Description: 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String phone);

    public UserBaseInfo queryUserInfoByPhone(String phone);
}
