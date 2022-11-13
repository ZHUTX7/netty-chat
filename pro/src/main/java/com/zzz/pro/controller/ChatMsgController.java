package com.zzz.pro.controller;

import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.ChatMsgService;
import com.zzz.pro.utils.JWTUtils;
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

    //获取未签收消息
    @GetMapping("/getUnSignMsg")
    public SysJSONResult<List<ChatMsg>> getUnSignMsg(@RequestHeader("token") String token){
        String userId =  JWTUtils.getClaim(token,"userId");
        Map<String,Object> map  = new HashMap<>();
        List<ChatMsg> list = chatMsgService.getUnSignMsg(userId);
        map.put("messageList",list);
        map.put("sum",list.size());
        return ResultVOUtil.success(map);
    }
}
