package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.form.*;
import com.mindset.ameeno.controller.vo.LoginResultVO;
import com.mindset.ameeno.controller.vo.UserGpsVO;
import com.mindset.ameeno.controller.vo.UserPhotoVO;
import com.mindset.ameeno.controller.vo.UserVO;
import com.mindset.ameeno.pojo.dto.UserBaseInfo;
import com.mindset.ameeno.pojo.dto.UserPersonalInfo;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component("userService")
@Service
public interface UserService {
    LoginResultVO userLogin(LoginForm loginForm);
    UserBaseInfo userRegister(String phone, String deviceId);
    SysJSONResult delUser(UserBaseInfo userBaseInfo );
    SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo );

    SysJSONResult updateUserProfile(UpdateProfileForm userPersonalInfo );
    //查询用户信息
    UserVO queryUserVO(String userId);

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

    void updatePhone(UpdatePhoneForm form);

    void deleteUserBlackList(String userId);

    UserBaseInfo newUserDataInit(String phone,String deviceId);

}
