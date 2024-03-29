package com.mindset.ameeno.controller;

import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.controller.vo.FriendsVO;
import com.mindset.ameeno.service.FriendsService;
import com.mindset.ameeno.utils.JWTUtils;
import com.mindset.ameeno.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description 好友模块
 * @Date 2023/8/2 23:59
 * @Version 1.0
 */

@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Resource
    private FriendsService friendsService;

    //    查询好友列表
    @GetMapping("/queryFriends")
    public SysJSONResult<FriendsVO> queryFriends(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(friendsService.queryFriendsList(userId));
    }

    //移除好友
    @GetMapping("/removeFriends")
    public SysJSONResult removeFriends(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        friendsService.removeFriendsRel(userId,targetId);
        return  ResultVOUtil.success();
    }


    //打开聊天框
    @GetMapping("/clickChatWindow")
    public SysJSONResult clickChatWindow(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(friendsService.getFriendsVO(userId,targetId));
    }
}
