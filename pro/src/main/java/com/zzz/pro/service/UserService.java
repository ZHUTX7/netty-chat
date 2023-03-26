package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.form.LoginForm;
import com.zzz.pro.pojo.form.UpdatePhotoIndexForm;
import com.zzz.pro.pojo.form.UpdateProfileForm;
import com.zzz.pro.pojo.form.UserGpsForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public interface UserService {
    LoginResultVO userLogin(LoginForm loginForm);
    UserBaseInfo userRegister(String phone,String deviceId);
    SysJSONResult delUser(UserBaseInfo userBaseInfo );
    SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo );
    SysJSONResult userLoginByToken(String token );
    SysJSONResult updateUserProfile(UpdateProfileForm userPersonalInfo );
    //查询用户信息
    UserPersonalInfo queryUserProfile(String userId);

    void changeUserGps(String userId, String gps);
    void updateUserTag(UserTag userTag);
    List<UserTagVO> queryUserTag(String userId);
    void addUserTag(UserTag userTag);
    void clearUserTag(UserTag userTag);

    //上传用户位置
    void uploadUserPos(UserGpsForm userGpsForm);
    //查询用户位置
    UserGpsVO queryUserPos(String  userId);

    //上传个人照片
    String uploadUserPhoto(MultipartFile file, String userId,Integer photoIndex);
    //查询用户照片
    List<UserPhotoVO> queryUserPhoto(String userId);

    //修改用户照片索引
    void updateUserPhotoIndex(UpdatePhotoIndexForm form);
}
