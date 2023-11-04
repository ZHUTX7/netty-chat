package com.zzz.pro.service;

import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.controller.vo.ChatMsgVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMsgService {
    @Resource
    private ChatMsgMapper chatMsgMapper;

    public void updateMsgStatus(List<String> msgIds) {
        chatMsgMapper.batchUpdateMsgSigned(msgIds);
    }

    public List<ChatMsgVO> getUnSignMsg(String userId) {
        List<ChatMsg> list = chatMsgMapper.getUnSignMsgVO(userId);
        if(CollectionUtils.isEmpty(list)){
            return  new ArrayList<>();
        }
        List<ChatMsgVO> voList = new ArrayList<>();
        for (ChatMsg chatMsg : list) {
            ChatMsgVO vo = new ChatMsgVO();
            vo.setMsgId(chatMsg.getMsgId());
            vo.setSendUserId(chatMsg.getSendUserId());
            vo.setMessageType(chatMsg.getMessageType());
            vo.setMessage(chatMsg.getMessage());
            vo.setSendTime(chatMsg.getSendTime().getTime());
            voList.add(vo);
        }
        return  voList;
    }
}
