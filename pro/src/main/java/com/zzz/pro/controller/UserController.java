package com.zzz.pro.controller;

import com.zzz.pro.pojo.InterfaceDto.LoginDTO;
import com.zzz.pro.pojo.bo.UserBO;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.RegisterVO;
import com.zzz.pro.service.UserService;
import com.zzz.pro.utils.Img2Base64;
import com.zzz.pro.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/login")
    public SysJSONResult login(@RequestBody LoginDTO loginDTO,@RequestHeader("token") String token){

        userService.userLogin(loginDTO.getLoginParams());
        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
         String type =   loginDTO.getLoginMethod();
         switch (type){
             case "NORMAL":return userService.userLogin(loginDTO.getLoginParams());
             case "VERIFY":return userService.userLoginByToken(token);
            // case "TOKEN":loginDTO.getLoginParams().get;;break;
             default:return SysJSONResult.errorMsg("登录类型不存在");
         }

    }

    //TODO 查询用户资料
    @PostMapping("/profile")
    public SysJSONResult profile(@RequestBody LoginDTO loginDTO,@RequestHeader("token") String token){


        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
        String type =   loginDTO.getLoginMethod();
        switch (type){
            case "NORMAL":return userService.userLogin(loginDTO.getLoginParams());
            case "VERIFY":return userService.userLoginByToken(token);
            // case "TOKEN":loginDTO.getLoginParams().get;;break;
            default:return SysJSONResult.errorMsg("登录类型不存在");
        }

    }
    //验证注册码
    @PostMapping("/register/verifyPhone")
    public SysJSONResult verifyCode(@RequestBody RegisterVO registerVO){
        //1.验证用户名及密码
        UserBaseInfo u = new UserBaseInfo();
        u.setUserPhone(registerVO.getUserPhone());
        return userService.userIsExist(u);

        //2. 保存用户ID， 将ID与channelID进行绑定，提交消息引擎


    }
    @PostMapping("/editUserProfile")
    public SysJSONResult editUserProfile(@RequestBody UserPersonalInfo userPersonalInfo) {
        return userService.updateUserProfile(userPersonalInfo);
    }

    // TODO： 手机号登录 or 注册
    @PostMapping("/register")
    public SysJSONResult register(@RequestBody RegisterVO registerVO){
        if(!registerVO.getVerifyCode().equals("6666")){
            return SysJSONResult.errorMsg("验证码错误");
        }
        System.out.printf(registerVO.getUserPhone());
        System.out.println(registerVO.getPassword());
        //1.验证用户名及密码
        UserBaseInfo u = new UserBaseInfo();
        u.setUserPhone(registerVO.getUserPhone());
        u.setUserPassword(registerVO.getPassword());
        return userService.userRegister(u);

        //2. 保存用户ID， 将ID与channelID进行绑定，提交消息引擎


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
            return SysJSONResult.errorMsg("头像文件为空！");
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


    //TODO 1 匹配接口
    @PostMapping("/match")
    public SysJSONResult match(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);
        return  userService.match(u);
    }

    //TODO 解除匹配
    @PostMapping("/delMatch")
    public SysJSONResult delMatch(@RequestBody UserMatch userMatch,@RequestHeader("token") String token){
        userMatch.setMyUserId(JWTUtils.getClaim(token,"userId"));
        return  userService.delMatch(userMatch);
    }

    @PostMapping("/getMatchPerson")
    public SysJSONResult getMatchPerson(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);

        return  userService.getMatchPerson(u);
    }
}
