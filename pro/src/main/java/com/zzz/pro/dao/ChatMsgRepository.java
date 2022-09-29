package com.zzz.pro.dao;

import com.zzz.pro.pojo.dto.ChatMsg;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMsgRepository {
    //查询信息
    List<ChatMsg> getMsg(ChatMsg chatMsg);

    //批量签收消息
    void batchUpdateMsgSigned(List<String> msgIds);
}
