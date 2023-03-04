package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.DatingStatusVO;
import com.zzz.pro.pojo.vo.FriendsVO;
import com.zzz.pro.pojo.vo.UserProfileVO;

import java.util.List;
import java.util.Map;

public interface SocialService {

    //开始匹配,用户进入匹配池
    void match(String  userId );

    //停止匹配（1.手动停止 2.退出APP ）
    void stopMatch(String userId);

    //查询匹配状态
    Map<String,String> queryMatchStatus(String userId);

    //TODO 推送匹配人选 list
    List<UserProfileVO> pushMatchUserList(UserFilterForm userFilterForm,String UserId);

    //查看匹配对象个人主页信息
    UserPersonalInfo getMatchPerson(UserBaseInfo userBaseInfo );

    //确认匹配
    SysJSONResult boostMatch(String userId,String targetId);
    //不喜欢该用户
    void unBoostMatch(String userId,String targetId);
    //删除已经匹配对象
    void delMatch(UserMatch userMatch );

    //添加用户到黑名单
    void addBlackUserList(String userId, List<String> targetUserIds);

    //清除黑名单
    void delBlackUserList(String userId);

    //同意约会
    void acceptDating(String userId ,String targetId,Integer status);

    //完成约会
    void completeDating(String userId ,String targetId);

    //查看约会状态
    DatingStatusVO queryDatingStatus(String userId ,String targetId);

    //建立好友关系
    void makeFriendsRel(String userId ,String targetId);
    //查询好友关系
    List<FriendsVO> queryFriendsList(String userId );
    //移除好友
    void removeFriendsRel(String userId ,String targetId);
    //删除好友数据
    void delFriendsData(String userId ,String targetId);

    FriendsVO getFriendsVO(String userId,String targetId);

}
