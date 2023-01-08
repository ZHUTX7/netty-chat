package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.utils.MyMapper;


import java.util.List;

public interface UserMatchMapper extends MyMapper<UserMatch> {
     String queryUnMatchUser(String userId);

     List<String> queryMatchUser(String userId);

     List<UserPersonalInfo> queryUnMatchUserList(String userId);
}