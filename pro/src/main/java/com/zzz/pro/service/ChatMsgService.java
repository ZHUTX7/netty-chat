package com.zzz.pro.service;

import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatMsgService {
    @Resource
    private ChatMsgMapper chatMsgMapper;

    public void updateMsgStatus(List<String> msgIds) {
        chatMsgMapper.batchUpdateMsgSigned(msgIds);
    }

    public List<ChatMsg> getUnSignMsg(String userId) {
        return  chatMsgMapper.getUnSignMsg(userId);
    }
}
