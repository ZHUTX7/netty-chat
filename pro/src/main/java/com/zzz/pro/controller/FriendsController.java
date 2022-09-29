package com.zzz.pro.controller;


import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.FriendsService;
import com.zzz.pro.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    private  FriendsService friendsService;

    @PostMapping("/match")
    public SysJSONResult match(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);
        return  friendsService.match(u);
    }


    @PostMapping("/delMatch")
    public SysJSONResult delMatch(@RequestBody UserMatch userMatch, @RequestHeader("token") String token){
        userMatch.setMyUserId(JWTUtils.getClaim(token,"userId"));
        return  friendsService.delMatch(userMatch);
    }

    @GetMapping("/getMatchPerson")
    public SysJSONResult getMatchPerson(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        UserBaseInfo u = new UserBaseInfo();
        u.setUserId(userId);

        return  friendsService.getMatchPerson(u);
    }


    @GetMapping("/test")
    public String qq(){
        return  "连通···";
    }
}
