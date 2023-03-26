package com.zzz.pro.mapper;

import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.dto.UserTag;
import com.zzz.pro.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTagMapper extends MyMapper<UserTag> {
    void updateUserTag(UserTag userTag);
    void insertUserTag(UserTag userTag);
}
