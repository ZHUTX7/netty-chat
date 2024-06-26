package com.mindset.ameeno.controller;


import com.mindset.ameeno.controller.form.*;
import com.mindset.ameeno.controller.vo.ChatMsgVO;
import com.mindset.ameeno.pojo.dto.UserBaseInfo;
import com.mindset.ameeno.service.UserService;
import com.mindset.ameeno.service.api.SmsService;
import com.mindset.ameeno.utils.JWTUtils;
import com.mindset.ameeno.utils.ResultVOUtil;
import com.mindset.ameeno.config.ApiLimit;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.UserTagService;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ztx
 * @date 2021-12-03 11:27
 * @description : 用户控制器
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private SmsService smsService;
    @Resource
    private UserTagService userTagService;


    @ApiLimit(seconds = 10,maxCount = 3)
    @PostMapping("/login")
    public SysJSONResult login(@RequestBody LoginForm loginForm){
        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
         String type =   loginForm.getLoginMethod();
//         if(!loginForm.getCountryCode().equals("CN")){
//                return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"暂不支持国外手机号登录");
//         }
         switch (type){
             case "NORMAL":return ResultVOUtil.success(userService.userLogin(loginForm));
             default:return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"登录类型不存在");
         }

    }

    @PostMapping("/editUserProfile")
    public SysJSONResult editUserProfile(@RequestBody UpdateProfileForm userPersonalInfo) {
        return userService.updateUserProfile(userPersonalInfo);
    }

    @PostMapping("/modifyUser")
    public SysJSONResult modifyUser(@RequestBody UserBaseInfo userBaseInfo){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        return  userService.delUser(userBaseInfo);
    }


    //测试 - 删除用户
    @PostMapping("/delUser")
    public SysJSONResult delUser(@RequestBody UserBaseInfo userBaseInfo){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        return  userService.delUser(userBaseInfo);
    }


    @PostMapping("/addUserTag")
    public SysJSONResult addUserTag(@RequestBody UserTagForm form){
        //userService.addUserTag(userTag);
        userTagService.batchInsertOrUpdateUserTag(form);
        return  ResultVOUtil.success();
    }

    @GetMapping("/queryUserTag")
    public SysJSONResult queryUserTag(@Param("userId") String userId){
        return  ResultVOUtil.success(userTagService.queryUserTag(userId));
    }

    @PostMapping("/clearUserTag")
    public SysJSONResult clearUserTag(@RequestBody DeleteTagForm form){
        userTagService.clearUserTag(form);
        return  ResultVOUtil.success();
    }


    //上传用户位置
    @PostMapping("/uploadUserPos")
    public SysJSONResult uploadUserPos(@RequestBody @Valid UserGpsForm userGpsForm){
        userService.uploadUserPos(userGpsForm);
        return  ResultVOUtil.success();
    }
    // 查询用户距离目标地点位置
    @GetMapping("/queryUserDistance")
    public SysJSONResult uploadUserPos(@Param("targetId") String targetId){
        if (targetId == null || targetId.equals("")){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"目标id不能为空！");
        }
        return  ResultVOUtil.success(userService.queryUserPos(targetId));
    }


    //查询用户图片
    @GetMapping("/queryUserPhoto")
    public SysJSONResult queryUserPhoto(@Param("userId") String userId){
        if (userId == null || userId.equals("")){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"用户id不能为空！");
        }
        return  ResultVOUtil.success(userService.queryUserPhoto(userId));
    }

    //查询用户信息
    @GetMapping("/queryUserInfo")
    public SysJSONResult queryUserInfo(@Param("userId") String userId){
        if (userId == null || userId.equals("")){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"用户id不能为空！");
        }
        return  ResultVOUtil.success(userService.queryUserVO(userId));
    }
    //发送短信
    @GetMapping("/sendSms")
    public SysJSONResult sendSms(@Param("phone") String phone,@Param("regionCode") String regionCode,@Param("countryCode") String countryCode ,HttpServletRequest request){
        if (phone == null || phone.equals("")){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),"手机号不能为空！");
        }
        return  ResultVOUtil.success(smsService.sendSms(regionCode,phone,countryCode));
    }

    //修改照片位置
    @PostMapping("/photo/updateIndex")
    public SysJSONResult updateUserPhotoIndex(@RequestBody @Valid UpdatePhotoIndexForm form, BindingResult result){
        if(result.hasErrors()){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),result.getFieldError().getDefaultMessage());
        }
        userService.updateUserPhotoIndex(form);
        return  ResultVOUtil.success();
    }
    //完成真人验证
    @PostMapping("/realAuth")
    public SysJSONResult realAuth(@RequestBody @Valid UserRealAuthForm form, BindingResult result){
        if(result.hasErrors()){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),result.getFieldError().getDefaultMessage());
        }
        userService.userRealAuth(form);
        return  ResultVOUtil.success();

    }

    //TEST 清除用户不喜欢列表
    @GetMapping("/clear/black")
    public void clearBlackList(@RequestParam("userId") String userId){

    }

    //修改用户手机号
    @PostMapping("/update/phone")
    public SysJSONResult updatePhone(@RequestBody @Valid UpdatePhoneForm form, BindingResult result){
        if(result.hasErrors()){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),result.getFieldError().getDefaultMessage());
        }
        userService.updatePhone(form);
        return  ResultVOUtil.success();
    }



    @GetMapping("/logout")
    public SysJSONResult logout(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        userService.userLogout(userId);
        return ResultVOUtil.success();
    }

}
