package com.zzz.pro.service;

import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public class FriendsServiceImpl implements FriendsService{

    @Resource
    UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SysJSONResult match(UserBaseInfo userBaseInfo) {

        //查询是否已经匹配

        UserMatch userMatch = userRepository.queryUserMatch(userBaseInfo);
        if(userMatch != null){
            return SysJSONResult.errorMsg("匹配失败,已经有匹配对象");
        }
        UserPersonalInfo u =  userRepository.queryUnMatchUser(userBaseInfo);
        if(u==null){
            return SysJSONResult.errorMsg("匹配失败");
        }
        //默认确定匹配
        userMatch = new UserMatch();
        userMatch.setMyUserId(userBaseInfo.getUserId());
        userMatch.setMatchUserId(u.getUserId());
        userMatch.setActiveState(1);
        //互相添加
        try{
            int  a = userRepository.addMatchUsers(userMatch);
            userMatch.setMyUserId(u.getUserId());
            userMatch.setMatchUserId(userBaseInfo.getUserId());
            int b = userRepository.addMatchUsers(userMatch);

            if( a == b && a == 1){
                return SysJSONResult.ok(u,"匹配用户成功");
            }else {
                throw new RuntimeException();
            }
        }catch (Exception e){
            return SysJSONResult.errorMsg("匹配失败");
        }



    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SysJSONResult delMatch(UserMatch userMatch) {
        try{
            String a = userMatch.getMyUserId();
            String b = userMatch.getMatchUserId();
            userRepository.delMatchUsers(userMatch);
            userMatch.setMyUserId(b);
            userMatch.setMatchUserId(a);
            userRepository.delMatchUsers(userMatch);
            return SysJSONResult.ok("解除匹配～");

        }catch (Exception e){
            return SysJSONResult.ok("解除失败～");
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SysJSONResult getMatchPerson(UserBaseInfo userBaseInfo) {

        return SysJSONResult.ok(userRepository.getMatchPerson(userBaseInfo.getUserId()));

    }
}
