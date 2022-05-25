package com.zzz.pro.dao;

import com.zzz.pro.pojo.dto.ChatMsg;

import java.util.List;

public interface ChatMsgRepository {
    //查询信息
    List<ChatMsg> getMsg(ChatMsg chatMsg);
}
