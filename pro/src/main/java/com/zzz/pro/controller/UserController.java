package com.zzz.pro.controller;


import com.zzz.pro.config.ApiLimit;
import com.zzz.pro.config.ApnsConfig;
import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.pojo.bo.UserBO;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.dto.UserTag;
import com.zzz.pro.pojo.form.LoginForm;
import com.zzz.pro.pojo.form.UpdatePhotoIndexForm;
import com.zzz.pro.pojo.form.UpdateProfileForm;
import com.zzz.pro.pojo.form.UserGpsForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.RegisterVO;
import com.zzz.pro.service.SmsService;
import com.zzz.pro.service.UserService;
import com.zzz.pro.utils.Img2Base64;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

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
    private ApnsConfig apnsConfig;
    @GetMapping("/test")
    public void test(){
        String deviceId = "48966b81137c9c077eda218216b11782e124e18dbc622ce1493bd2c2eeb300d7";
        String json = "{\"msg\":\"hello\"}";
        apnsConfig.sendIosMsg(deviceId,json,10000);
    }
    @ApiLimit(seconds = 10,maxCount = 3)
    @PostMapping("/login")
    public SysJSONResult login(@RequestBody LoginForm loginForm, @RequestHeader("token") String token){
        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
         String type =   loginForm.getLoginMethod();
         if(!loginForm.getCountryCode().equals("CN")){
                return ResultVOUtil.error(401,"暂不支持国外手机号登录");
         }
         switch (type){
             case "NORMAL":return ResultVOUtil.success(userService.userLogin(loginForm));
             case "VERIFY":return userService.userLoginByToken(token);
            // case "TOKEN":loginDTO.getLoginParams().get;;break;
             default:return ResultVOUtil.error(401,"登录类型不存在");
         }

    }

    @PostMapping("/editUserProfile")
    public SysJSONResult editUserProfile(@RequestBody UpdateProfileForm userPersonalInfo) {
        return userService.updateUserProfile(userPersonalInfo);
    }



    @PostMapping("/uploadFaceImageBig")
    @CrossOrigin(maxAge = 3699,origins = "*")
    public SysJSONResult uploadFaceImageBig(@RequestBody UserBO userBO){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userBO.getUserId());
        userPersonalInfo.setUserFaceImage(base64Data);
        userPersonalInfo.setUserFaceImageBig(base64Data);
        //TODO： 将文件上传到文件服务器
        return userService.uploadFaceImg(userPersonalInfo);

    }

    @PostMapping("/uploadFaceImageFile")
    @CrossOrigin(maxAge = 3699,origins = "*")
    public SysJSONResult uploadFaceImageFile(@RequestParam("files") MultipartFile files,HttpServletRequest request) throws IOException {
        String userId =  request.getParameter("userId");
        if(files.isEmpty()||files.getSize()==0||files.getInputStream()==null){
            return ResultVOUtil.error(401,"头像文件为空！");
        }
        String base64Data =  Img2Base64.getImageInput(files.getInputStream());
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userId);
        userPersonalInfo.setUserFaceImage(base64Data);
        userPersonalInfo.setUserFaceImageBig(base64Data);
        System.out.println("文件长度： "+files.getSize());
        return userService.uploadFaceImg(userPersonalInfo);

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

    @PostMapping("/updateUserTag")
    public SysJSONResult updateUserTag(@RequestBody UserTag userTag){
        userService.updateUserTag(userTag);
        return  ResultVOUtil.success();
    }

    @PostMapping("/addUserTag")
    public SysJSONResult addUserTag(@RequestBody UserTag userTag){
        userService.addUserTag(userTag);
        return  ResultVOUtil.success();
    }

    @GetMapping("/queryUserTag")
    public SysJSONResult queryUserTag(@Param("userId") String userId){
        return  ResultVOUtil.success(userService.queryUserTag(userId));
    }

    @PostMapping("/clearUserTag")
    public SysJSONResult clearUserTag(@RequestBody UserTag userTag){
        userService.clearUserTag(userTag);
        return  ResultVOUtil.success();
    }

    //上传用户位置
    @PostMapping("/uploadUserPos")
    public SysJSONResult uploadUserPos(@RequestBody UserGpsForm userGpsForm){
        userService.uploadUserPos(userGpsForm);
        return  ResultVOUtil.success();
    }
    // 查询用户距离目标地点位置
    @GetMapping("/queryUserDistance")
    public SysJSONResult uploadUserPos(@Param("targetId") String targetId){
        return  ResultVOUtil.success(userService.queryUserPos(targetId));
    }

    //上传用户照片
    @PostMapping("/uploadUserPhoto")
    public SysJSONResult uploadUserPhoto(@RequestParam("files") MultipartFile files,HttpServletRequest request) throws IOException {
        String userId = request.getParameter("userId");
        Integer photoIndex = Integer.parseInt(request.getParameter("photoIndex")) ;
           if(files.isEmpty()||files.getSize()==0||files.getInputStream()==null){
                return ResultVOUtil.error(401,"照片为空！");
            }
        return  ResultVOUtil.success( userService.uploadUserPhoto(files,userId,photoIndex));
    }

    //查询用户图片
    @GetMapping("/queryUserPhoto")
    public SysJSONResult queryUserPhoto(@Param("userId") String userId){
        return  ResultVOUtil.success(userService.queryUserPhoto(userId));
    }

    //查询用户信息
    @GetMapping("/queryUserInfo")
    public SysJSONResult queryUserInfo(@Param("userId") String userId){
        return  ResultVOUtil.success(userService.queryUserProfile(userId));
    }
    //发送短信
    @GetMapping("/sendSms")
    public SysJSONResult sendSms(@Param("phone") String phone){
        return  ResultVOUtil.success(smsService.sendSms(phone));
    }

    //修改照片位置
    @PostMapping("/updateUserPhotoIndex")
    public SysJSONResult updateUserPhotoIndex(@RequestBody @Valid UpdatePhotoIndexForm form, BindingResult result){
        if(result.hasErrors()){
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),result.getFieldError().getDefaultMessage());
        }
        userService.updateUserPhotoIndex(form);
        return  ResultVOUtil.success();
    }
}
