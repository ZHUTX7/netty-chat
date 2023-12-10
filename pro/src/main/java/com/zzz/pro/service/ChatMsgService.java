package com.zzz.pro.service;

import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.controller.vo.ChatMsgVO;
import com.zzz.pro.utils.PushUtils;
import com.zzz.pro.utils.RedisStringUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMsgService {
    @Resource
    private ChatMsgMapper chatMsgMapper;
    @Resource
    private PushUtils pushUtils;
    @Resource
    private RedisStringUtil redisStringUtil;


    public void updateMsgStatus(String userId, List<String> msgIds) {
        chatMsgMapper.batchUpdateMsgSigned(msgIds);
        clearUnReadMsgCount(userId,msgIds.size());
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
        if(!CollectionUtils.isEmpty(voList)){
            clearUnReadMsgCount(userId,voList.size());
        }
        return  voList;
    }

    @Async
    public void clearUnReadMsgCount(String userId,int size){
        //删除缓存计数
        redisStringUtil.decr(RedisKeyEnum.USER_UNREAD_MSG_COUNT.getCode()+userId,size);
        //删除角标
        String deviceId = redisStringUtil.get(RedisKeyEnum.USER_DEVICE_ID.getCode()+userId);
        pushUtils.clearIosBadge(deviceId);

    }
}
