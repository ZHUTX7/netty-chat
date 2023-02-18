package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.pojo.vo.UserVO;
import com.zzz.pro.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface UserPersonalInfoMapper extends MyMapper<UserPersonalInfo> {
      List<Map> queryUserOutlineInfo(List<String> userIdList);
}