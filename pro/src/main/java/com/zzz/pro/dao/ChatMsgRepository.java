package com.zzz.pro.dao;

import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatMsgRepository {
    @Resource
    private ChatMsgMapper chatMsgMapper;

    //查询信息
    public List<ChatMsg> getMsg(ChatMsg chatMsg) {
        return chatMsgMapper.select(chatMsg);
    }

    //批量签收信息
    public void batchUpdateMsgSigned(List<String> msgIds) {
        chatMsgMapper.batchUpdateMsgSigned(msgIds);
    }
}
