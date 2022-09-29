package com.zzz.pro.service;

import com.zzz.pro.dao.ChatMsgRepository;
import com.zzz.pro.enums.MsgSignFlagEnum;
import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMsgServiceImpl implements ChatMsgService{

    @Resource
    private ChatMsgMapper chatMsgMapper;

    @Override
    public void updateMsgStatus(List<String> msgIds) {
        chatMsgMapper.batchUpdateMsgSigned(msgIds);
    }

    @Override
    public List<ChatMsg> getUnSignMsg(String userId) {
        return  chatMsgMapper.getUnSignMsg(userId);
    }
}
