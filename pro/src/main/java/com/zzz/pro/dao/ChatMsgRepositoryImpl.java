package com.zzz.pro.dao;

import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;

import javax.annotation.Resource;
import java.util.List;

public class ChatMsgRepositoryImpl implements ChatMsgRepository{
    @Resource
    private ChatMsgMapper chatMsgMapper;
    @Override
    public List<ChatMsg> getMsg(ChatMsg chatMsg) {
        return chatMsgMapper.select(chatMsg);
    }
}
