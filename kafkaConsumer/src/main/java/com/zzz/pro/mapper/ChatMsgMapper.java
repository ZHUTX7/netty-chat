package com.zzz.pro.mapper;


import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.MyMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMsgMapper extends MyMapper<ChatMsg> {
    @Insert(" INSERT INTO chat_msg ( msg_id,send_user_id,accept_user_id,message,sign_flag,message_type,send_time ) " +
            "VALUES (#{msg.msgId},#{msg.sendUserId},#{msg.acceptUserId}," +
            "#{msg.message},#{msg.signFlag},#{msg.messageType},#{msg.sendTime}) ;")
    int insertMsg(@Param("msg")ChatMsg msg);
}