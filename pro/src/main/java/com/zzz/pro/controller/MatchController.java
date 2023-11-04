package com.zzz.pro.controller;

import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.controller.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.MatchService;
import com.zzz.pro.service.RecommendPoolService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description 匹配模块
 * @Date 2023/8/2 23:57
 * @Version 1.0
 */
@RestController
@RequestMapping("/match")
public class MatchController {
    @Resource
    private MatchService matchService;
    @Resource
    private RecommendPoolService recommendPoolService;

    //开始匹配
    @GetMapping("/start")
    public SysJSONResult match(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        matchService.match(userId);
        return ResultVOUtil.success("正在匹配 ~");
    }

    //停止匹配
    @GetMapping("/stop")
    public SysJSONResult stopMatch(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        matchService.stopMatch(userId);
        return ResultVOUtil.success("停止匹配 ~");
    }

    //查看匹配状态
    @GetMapping("/status/query")
    public SysJSONResult queryMatchStatus(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return ResultVOUtil.success(matchService.queryMatchStatus(userId));
    }

    //解除已经匹配的对象
    @PostMapping("/release")
    public SysJSONResult delMatch(@RequestBody UserMatch userMatch, @RequestHeader("refreshToken") String token){
        userMatch.setMyUserId(JWTUtils.getClaim(token,"userId"));
        matchService.delMatch(userMatch);
        return  ResultVOUtil.success("解除匹配成功");
    }

    @GetMapping("/getMatchPerson")
    public SysJSONResult getMatchPerson(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(matchService.getMatchPerson(userId));
    }


    //推送匹配用户
    @PostMapping("/queryMatchingUser")
    public SysJSONResult queryMatchingUser(@RequestHeader("refreshToken") String token,@RequestBody UserFilterForm userFilterForm) throws InterruptedException {
        String userId =  JWTUtils.getClaim(token,"userId");

//        List<UserProfileVO> list = matchService.pushMatchUserList(userFilterForm,userId);
        return  ResultVOUtil.success(recommendPoolService.pullUserList(0,userId,999999999));
    }

    ///确认匹配
    @GetMapping("/boost")
    public SysJSONResult boost(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        matchService.boostMatch(userId,targetId);
        return  ResultVOUtil.success();
    }

    //取消匹配
    @GetMapping("/unBoost")
    public SysJSONResult unBoost(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        matchService.unBoostMatch(userId,targetId);
        return  ResultVOUtil.success();
    }

}
