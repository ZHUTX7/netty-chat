package com.zzz.pro.controller;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.service.FriendsService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Resource
    private  FriendsService friendsService;


    //开始匹配
    @GetMapping("/match")
    public SysJSONResult match(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        friendsService.match(userId);
        return ResultVOUtil.success("正在匹配 ~");
    }

    //停止匹配
    @GetMapping("/stopMatch")
    public SysJSONResult stopMatch(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        friendsService.stopMatch(userId);
        return ResultVOUtil.success("停止匹配 ~");
    }

    //解除已经匹配的对象
    @PostMapping("/delMatch")
    public SysJSONResult delMatch(@RequestBody UserMatch userMatch, @RequestHeader("token") String token){
        userMatch.setMyUserId(JWTUtils.getClaim(token,"userId"));
        friendsService.delMatch(userMatch);
        return  ResultVOUtil.success("解除匹配成功");
    }

    //查询匹配到的对象
    @GetMapping("/getMatchPerson")
    public SysJSONResult getMatchPerson(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);
        return  ResultVOUtil.success(friendsService.getMatchPerson(u));
    }


    //推送匹配用户
    @PostMapping("/queryMatchingUser")
    public SysJSONResult queryMatchingUser(@RequestBody UserFilterForm userFilterForm){
        List<UserProfileVO>  list = friendsService.pushMatchUserList(userFilterForm);
        return  ResultVOUtil.success(list);
    }

    ///确认匹配
    @PostMapping("/boost")
    public SysJSONResult boost(@RequestHeader("token") String token,@RequestBody String targetUserId){
        String userId =  JWTUtils.getClaim(token,"userId");
        friendsService.boostMatch(userId,targetUserId);
        return  ResultVOUtil.success();
    }

    //取消匹配
    @PostMapping("/unBoost")
    public SysJSONResult unBoost(@RequestHeader("token") String token,@RequestBody String targetUserId){
        String userId =  JWTUtils.getClaim(token,"userId");
        friendsService.boostMatch(userId,targetUserId);
        return  ResultVOUtil.success();
    }

}
