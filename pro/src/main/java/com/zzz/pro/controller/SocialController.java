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

    //查看匹配状态
    @GetMapping("/queryMatchStatus")
    public SysJSONResult queryMatchStatus(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return ResultVOUtil.success(socialService.queryMatchStatus(userId));
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
        return  ResultVOUtil.success(socialService.getMatchPerson(userId));
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
        socialService.unBoostMatch(userId,targetId);
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
        return  ResultVOUtil.success(socialService.queryDatingStatus(userId,targetId));
    }
    //提出约会/同意约会/修改约会状态
    @GetMapping("/dating/accept")
    public SysJSONResult acceptDating(@RequestHeader("token") String token,@Param("targetId") String targetId,
                                      @Param("action") Integer action){
        //action = 1 提出约会
        //action = 5 自己到达约会地点
        if(action!=1 && action!=5){
            return  ResultVOUtil.success("别玩骚的",null);
        }
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.acceptDating(userId,targetId,action);
        return  ResultVOUtil.success();
    }

    //    查询好友列表
    @GetMapping("/queryFriends")
    public SysJSONResult queryFriends(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(socialService.queryFriendsList(userId));
    }

    //移除好友
    @GetMapping("/removeFriends")
    public SysJSONResult removeFriends(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        socialService.removeFriendsRel(userId,targetId);
        return  ResultVOUtil.success();
    }


    //打开聊天框
    @GetMapping("/clickChatWindow")
    public SysJSONResult clickChatWindow(@RequestHeader("token") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(socialService.getFriendsVO(userId,targetId));
    }

}
