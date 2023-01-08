package com.zzz.pro.controller;

import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.service.SocialService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/friends")
public class SocialController {

    @Resource
    private SocialService socialService;


    //开始匹配
    @GetMapping("/match")
    public SysJSONResult match(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.match(userId);
        return ResultVOUtil.success("正在匹配 ~");
    }

    //停止匹配
    @GetMapping("/stopMatch")
    public SysJSONResult stopMatch(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.stopMatch(userId);
        return ResultVOUtil.success("停止匹配 ~");
    }

    //解除已经匹配的对象
    @PostMapping("/delMatch")
    public SysJSONResult delMatch(@RequestBody UserMatch userMatch, @RequestHeader("token") String token){
        userMatch.setMyUserId(JWTUtils.getClaim(token,"userId"));
        socialService.delMatch(userMatch);
        return  ResultVOUtil.success("解除匹配成功");
    }

    // TODO  返回List 查询匹配到的对象
    @GetMapping("/getMatchPerson")
    public SysJSONResult getMatchPerson(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);
        return  ResultVOUtil.success(socialService.getMatchPerson(u));
    }


    //推送匹配用）
    @PostMapping("/queryMatchingUser")
    public SysJSONResult queryMatchingUser(@RequestHeader("token") String token,@RequestBody UserFilterForm userFilterForm){
        String userId =  JWTUtils.getClaim(token,"userId");

        List<UserProfileVO>  list = socialService.pushMatchUserList(userFilterForm,userId);
        return  ResultVOUtil.success(list);
    }

    ///确认匹配
    @GetMapping("/boost")
    public SysJSONResult boost(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.boostMatch(userId,targetId);
        return  ResultVOUtil.success();
    }

    //取消匹配
    @GetMapping("/unBoost")
    public SysJSONResult unBoost(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.boostMatch(userId,targetId);
        return  ResultVOUtil.success();
    }

    //TODO 完成约会
    @GetMapping("/dating/complete")
    public SysJSONResult complete(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.completeDating(userId,targetId);
        return  ResultVOUtil.success();
    }

    //查看约会状态
    @GetMapping("/dating/query")
    public SysJSONResult queryDatingStatus(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
      ;
        return  ResultVOUtil.success(socialService.queryDatingStatus(userId,targetId));
    }
    //提出约会/同意约会
    @GetMapping("/dating/accept")
    public SysJSONResult acceptDating(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.acceptDating(userId,targetId);
        return  ResultVOUtil.success();
    }

    //    查询好友
    @GetMapping("/queryFriends")
    public SysJSONResult queryFriends(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(socialService.queryFriendsList(userId));
    }

    //删除好友
    @PostMapping("/removeFriends")
    public SysJSONResult removeFriends(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(socialService.queryFriendsList(userId));
    }


}
