package com.zzz.pro.controller;

import com.zzz.pro.pojo.bo.UserBO;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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

    @GetMapping("/login")
    public SysJSONResult login(@RequestBody UserBaseInfo userBaseInfo){
        //test
        System.out.printf(userBaseInfo.getUserPhone());
        //1.验证用户名及密码
        return userService.userLogin(userBaseInfo);

        //2. 保存用户ID， 将ID与channelID进行绑定，提交消息引擎

    }

    @PostMapping("/uploadFaceImageBig")
    @CrossOrigin(maxAge = 3699,origins = "*")
    public SysJSONResult uploadFaceImageBig(@RequestBody UserBO userBO){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();


        //TODO： 将文件上传到文件服务器



        // 更细用户头像
//        Users user = new Users();
//        user.setId(userBO.getUserId());
//        user.setFaceImage(thumpImgUrl);
//        user.setFaceImageBig(url);
//
//        Users result = userService.updateUserInfo(user);

        return null;
    }
}
