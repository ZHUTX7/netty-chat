package com.zzz.pro.controller;

import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.controller.vo.ChatMsgVO;
import com.zzz.pro.controller.vo.PushMsgVO;
import com.zzz.pro.service.ChatMsgService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.PushUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatMsgController {
    @Resource
    private ChatMsgService chatMsgService;
    @Resource
    private PushUtils pushUtils;
    //获取未签收消息
    @GetMapping("/getUnSignMsg")
    public SysJSONResult<Object> getUnSignMsg(@RequestHeader("refreshToken") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        Map<String,Object> map  = new HashMap<>();
        List<ChatMsgVO> list = chatMsgService.getUnSignMsg(userId);
        map.put("messageList",list);
        map.put("sum",list.size());
        return ResultVOUtil.success(map);
    }
    //TODO 批量签收消息
    //@PostMapping


    @PostMapping("/test/apns")
    public SysJSONResult<Object> getUnSignMsg(@RequestHeader("deviceId") String deviceId, @RequestBody PushMsgVO vo){
        pushUtils.pushMsg(vo,deviceId);
        return ResultVOUtil.success();
    }

}
