package com.zzz.pro.service;

import com.zzz.pro.dao.ChatMsgRepository;
import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.utils.IDWorker;
import com.zzz.pro.utils.JWTUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserRepository userRepository;

    @Resource
    private ChatMsgRepository chatMsgRepository;
    private IDWorker idWorker = new IDWorker(1,1,1);

    @Override
    public SysJSONResult userLogin(UserBaseInfo userBaseInfo) {
        // 1. 验证用户名是否存在
        if(!userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return SysJSONResult.errorMsg("用户名不存在");
        }
        // 2. 验证密码
        UserBaseInfo userBaseInfo1 =  userRepository.queryUserInfo(userBaseInfo.getUserPhone(),userBaseInfo.getUserPassword());

        if(userBaseInfo1 == null){
            return SysJSONResult.errorMsg("密码错误。");
        }
        //3. 获取用户信息
        UserPersonalInfo u = userRepository.queryUserPerInfo(userBaseInfo1.getUserId());

        // 4. 创建token
        Map<String ,String > info =  new HashMap<>();
        info.put("nickname",u.getUserNickname());
        info.put("userId",u.getUserId());
//        info.put("userFaceImage",u.getUserFaceImage());
        String token = JWTUtils.creatToken(info);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",u.getUserId());
        map.put("token",token);

        //开发测试
//        u.setUserFaceImageBig("测试数据，不显示");
//        u.setUserFaceImage("测试数据，不显示");
        map.put("profile",u);
        userBaseInfo1.setUserLoginState(1);
        userRepository.updateUserStatus(userBaseInfo1);
        UserMatch userMatch =  userRepository.queryUserMatch(userBaseInfo1);
        map.put("account",userBaseInfo1);
        if(userMatch !=null ){
            map.put("isMatch",1);
            map.put("matchedUser",userRepository.queryUserPerInfo(userMatch.getMatchUserId()));

        }else{
            map.put("isMatch",0);
        }

        return SysJSONResult.ok(map);
    }

    @Override
    public SysJSONResult userRegister(UserBaseInfo userBaseInfo) {
        if(userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return SysJSONResult.errorMsg("手机号被占用");
        }
        userBaseInfo.setUserId(idWorker.nextId()+"");
        userRepository.addUserBaseInfo(userBaseInfo);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userBaseInfo.getUserId());

        userRepository.addUserPersonalInfo(userPersonalInfo);
        Map<String,Object> map  = new HashMap<>();
        map.put("account",userBaseInfo);
        map.put("profile",userPersonalInfo);
        map.put("isMatch",0);
        return SysJSONResult.ok(userBaseInfo,"用户注册成功");
    }

    @Override
    public SysJSONResult userIsExist(UserBaseInfo userBaseInfo) {
        if(userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return SysJSONResult.errorMsg("手机号被占用");
        }

        return SysJSONResult.ok();
    }

    @Override
    public SysJSONResult delUser(UserBaseInfo userBaseInfo) {
        int result =  userRepository.delUser(userBaseInfo);
        if(result == 1) {
            return SysJSONResult.ok("删除成功");
        }
        else{
            return SysJSONResult.errorMsg("删除失败，用户不存在");
        }

    }

    @Override
    public SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo) {
        int result =    userRepository.updateUserFace(userPersonalInfo);
        if(result == 1) {
            return SysJSONResult.ok("更新头像成功");
        }
        else{
            return SysJSONResult.errorMsg("更新头像失败");
        }
    }

    @Override
    public SysJSONResult userLoginByToken(String token) {
        if(StringUtil.isNullOrEmpty(token)){
            return SysJSONResult.errorMsg("用户登录状态过期");
        }
        int token_code =  (int)JWTUtils.verify(token).get("token_code");
        if(token_code != 1 ){
            return SysJSONResult.errorMsg("用户登录状态过期");
        }
        String userId = JWTUtils.getClaim(token,"userId");
        UserPersonalInfo u = userRepository.queryUserPerInfo(userId);


        Map<String,Object> map = new HashMap<>();
        map.put("userId",u.getUserId());
        map.put("token",token);
        map.put("userPersonalInfo",u);
        return SysJSONResult.ok(map);

    }

    @Override
    public SysJSONResult updateUserProfile(UserPersonalInfo userPersonalInfo) {
      int result =  userRepository.updateUserProfile(userPersonalInfo);
      return SysJSONResult.ok(null,"更新用户资料成功");
    }

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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public SysJSONResult getUnReadMessage(UserBaseInfo userBaseInfo) {
        try{
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setAcceptUserId(userBaseInfo.getUserId());
            chatMsg.setSignFlag(0);
            List<ChatMsg> list = chatMsgRepository.getMsg(chatMsg);
            return SysJSONResult.ok(list,"查询成功！");

        }catch (Exception e){
            return SysJSONResult.ok("查询失败～");
        }

    }


}
