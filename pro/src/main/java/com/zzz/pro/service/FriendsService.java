package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserProfileVO;

import java.util.List;

public interface FriendsService {

    //开始匹配,用户进入匹配池
    void match(String  userId );

    //停止匹配（1.手动停止 2.退出APP ）
    void stopMatch(String userId);

    //TODO 推送匹配人选 list
    List<UserProfileVO> pushMatchUserList(UserFilterForm userFilterForm);

    //查看匹配对象个人主页信息
    UserPersonalInfo getMatchPerson(UserBaseInfo userBaseInfo );

    //确认匹配
    SysJSONResult boostMatch(String userId,String targetId);
    //删除已经匹配对象
    void delMatch(UserMatch userMatch );



}
