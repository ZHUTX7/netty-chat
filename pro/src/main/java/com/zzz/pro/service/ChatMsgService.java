package com.zzz.pro.service;

import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMsgService {
    //批量签收消息
    void updateMsgStatus(List<String> msgIds);
    // 获取当前用户未读消息
    List<ChatMsg> getUnSignMsg(String userId);
}
