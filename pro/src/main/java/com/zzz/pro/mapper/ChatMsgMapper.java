package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMsgMapper extends MyMapper<ChatMsg> {
    void batchUpdateMsgSigned(List<String> msgIds);

    List<ChatMsg> getUnSignMsg(String userId);
}