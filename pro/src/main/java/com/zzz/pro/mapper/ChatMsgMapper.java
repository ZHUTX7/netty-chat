package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMsgMapper extends MyMapper<ChatMsg> {
    void batchUpdateMsgSigned(List<String> msgIds);

//    List<ChatMsg> getUnSignMsg(String userId);

//    @Select("select msg_id,send_user_id,message_type,message,send_time from chat_msg where accept_user_id = #{userId} and sign_flag = 0")
//    List<ChatMsg> getUnSignMsgVO(String userId);

    @Select("select msg_id,send_user_id,message_type,message,send_time from chat_msg where accept_user_id = #{userId} and sign_flag = 0")
    List<ChatMsg> getUnSignMsgVO(String userId);
    
}