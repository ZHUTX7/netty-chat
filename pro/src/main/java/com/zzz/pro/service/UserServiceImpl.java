package com.zzz.pro.service;

import com.zzz.pro.dao.ChatMsgRepository;
import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.dao.UserTagRepository;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserTagVO;
import com.zzz.pro.utils.IDWorker;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserRepository userRepository;
    @Resource
    private UserTagRepository userTagRepository;

    @Resource
    private ChatMsgRepository chatMsgRepository;

    private IDWorker idWorker = new IDWorker(1,1,1);

    @Override
    public SysJSONResult userLogin(UserBaseInfo userBaseInfo) {
        // 1. 验证用户名是否存在
        if(!userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return ResultVOUtil.error(500,"用户名不存在");
        }
        // 2. 验证密码
        UserBaseInfo userBaseInfo1 =  userRepository.queryUserInfo(userBaseInfo.getUserPhone(),userBaseInfo.getUserPassword());

        if(userBaseInfo1 == null){
            return ResultVOUtil.error(500,"密码错误");
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

        return ResultVOUtil.success(map);
    }

    @Override
    public SysJSONResult userRegister(UserBaseInfo userBaseInfo) {
        if(userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return ResultVOUtil.error(500,"手机号被占用");
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
        return ResultVOUtil.success("用户注册成功",userBaseInfo);
    }

    @Override
    public SysJSONResult userIsExist(UserBaseInfo userBaseInfo) {
        if(userRepository.queryPhoneIsExist(userBaseInfo.getUserPhone())){
            return ResultVOUtil.error(500,"手机号被占用");

        }

        return ResultVOUtil.success();
    }

    @Override
    public SysJSONResult delUser(UserBaseInfo userBaseInfo) {
        int result =  userRepository.delUser(userBaseInfo);
        if(result == 1) {
            return ResultVOUtil.success("删除成功");
        }
        else{
            return ResultVOUtil.error(500,"删除失败，用户不存在");
        }

    }

    @Override
    public SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo) {
        int result =    userRepository.updateUserFace(userPersonalInfo);
        if(result == 1) {
            return ResultVOUtil.success("更新头像成功");
        }
        else{
            return ResultVOUtil.error(500,"更新头像失败");
        }
    }

    @Override
    public SysJSONResult userLoginByToken(String token) {
        if(StringUtil.isNullOrEmpty(token)){

            return  ResultVOUtil.error(500,"用户登录状态过期");
        }
        int token_code =  (int)JWTUtils.verify(token).get("token_code");
        if(token_code != 1 ){
            return ResultVOUtil.error(500,"用户登录状态过期");
        }
        String userId = JWTUtils.getClaim(token,"userId");
        UserPersonalInfo u = userRepository.queryUserPerInfo(userId);


        Map<String,Object> map = new HashMap<>();
        map.put("userId",u.getUserId());
        map.put("token",token);
        map.put("userPersonalInfo",u);
        return ResultVOUtil.success(map);

    }

    @Override
    public SysJSONResult updateUserProfile(UserPersonalInfo userPersonalInfo) {
      int result =  userRepository.updateUserProfile(userPersonalInfo);
      return ResultVOUtil.success(null,"更新用户资料成功");
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public SysJSONResult getUnReadMessage(UserBaseInfo userBaseInfo) {
        try{
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setAcceptUserId(userBaseInfo.getUserId());
            chatMsg.setSignFlag(0);
            List<ChatMsg> list = chatMsgRepository.getMsg(chatMsg);
            return ResultVOUtil.success(list);

        }catch (Exception e){
            return ResultVOUtil.error(500,"查询失败～");
        }

    }

    @Override
    public void updateUserTag(UserTag userTag) {
        userTagRepository.updateUserTag(userTag);
    }


    @Override
    public List<UserTagVO>  queryUserTag(String userId) {
        List<UserTag > list = userTagRepository.queryUserTag(userId);
        List<UserTagVO> result = list.stream().map(e->{
            UserTagVO vo = new UserTagVO();
            BeanUtils.copyProperties(e,vo);
            return vo;
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public void addUserTag(UserTag userTag) {
        try{
            userTagRepository.insertUserTag(userTag);
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(401,"数据错误");
        }

    }

    @Override
    public void clearUserTag(UserTag userTag) {
        try{
            userTagRepository.delUserTag(userTag.getUserId(),userTag.getUserKey());
        }catch (Exception e){
            throw new ApiException(401,"数据错误");
        }

    }
}
