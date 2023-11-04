package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.controller.form.*;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.controller.vo.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component("userService")
@Service
public interface UserService {
    LoginResultVO userLogin(LoginForm loginForm);
    UserBaseInfo userRegister(String phone,String deviceId);
    SysJSONResult delUser(UserBaseInfo userBaseInfo );
    SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo );

    SysJSONResult updateUserProfile(UpdateProfileForm userPersonalInfo );
    //查询用户信息
    UserPersonalInfo queryUserProfile(String userId);

    void changeUserGps(String userId, double[] gps);
    //上传用户位置
    void uploadUserPos(UserGpsForm userGpsForm);
    //查询用户位置
    UserGpsVO queryUserPos(String  userId);
    //查询用户照片
    List<UserPhotoVO> queryUserPhoto(String userId);

    //修改用户照片索引
    void updateUserPhotoIndex(UpdatePhotoIndexForm form);

    //用户真人认证
    void userRealAuth(UserRealAuthForm form);

}
