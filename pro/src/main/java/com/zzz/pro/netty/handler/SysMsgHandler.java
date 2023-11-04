package com.zzz.pro.netty.handler;

import com.zzz.pro.enums.MsgActionEnum;
import com.zzz.pro.netty.UserChannelMap;
import com.zzz.pro.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.HashMap;

@Component
public class SysMsgHandler {
    private static Integer SYS_MSG = MsgActionEnum.SYSTEM.getType();
    private HashMap userChannelMap = UserChannelMap.getInstance();
    //推送系统消息
    public void pushSysMsg(String userId,Object msg){
        if(StringUtils.isEmpty(userId) || null ==msg ){
            return;
        }
        Channel userChannel = (Channel) userChannelMap.get(userId);
        if(userChannel == null){
            return;
        }
        String json = JsonUtils.objectToJson(msg);
        userChannel.writeAndFlush(new TextWebSocketFrame(json));
    }



}
