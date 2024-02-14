package com.mindset.ameeno.controller.vo;

import com.mindset.ameeno.enums.IOSLocKeyEnum;
import com.mindset.ameeno.enums.MsgTypeEnum;
import com.mindset.ameeno.netty.enity.ChatMsg;
import lombok.Data;

@Data
public class PushMsgVO {
    private String title;
    private String sendUserId;
    private String sendUserName;
    private String content;
    private Integer msgType;
    private String locKey;

    public static PushMsgVO chatMsg2PushMsg(ChatMsg chatMsg){
        PushMsgVO pushMsgVO = new PushMsgVO();
        pushMsgVO.setSendUserName(chatMsg.getSendUserName());
        pushMsgVO.setMsgType(chatMsg.getMsgType());
        pushMsgVO.setSendUserId(chatMsg.getSenderId());
        if(chatMsg.getMsgType() == MsgTypeEnum.MESSAGE_TEXT.getCode()) {
            pushMsgVO.setContent(chatMsg.getMsg());
            return pushMsgVO;
        }
        if(chatMsg.getMsgType() == MsgTypeEnum.MESSAGE_VIDEO.getCode()) {
            pushMsgVO.setLocKey(IOSLocKeyEnum.MESSAGE_VIDEO.getCode());
            return pushMsgVO;
        }
        if(chatMsg.getMsgType() == MsgTypeEnum.MESSAGE_IMAGE.getCode()) {
            pushMsgVO.setLocKey(IOSLocKeyEnum.MESSAGE_IMAGE.getCode());
            return pushMsgVO;
        }
        if(chatMsg.getMsgType() == MsgTypeEnum.MESSAGE_SOUND.getCode()) {
            pushMsgVO.setLocKey(IOSLocKeyEnum.MESSAGE_AUDIO.getCode());
            return pushMsgVO;
        }
        if(chatMsg.getMsgType() == MsgTypeEnum.MESSAGE_GIF_EMOJI.getCode()) {
            pushMsgVO.setLocKey(IOSLocKeyEnum.MESSAGE_GIF.getCode());
            return pushMsgVO;
        }
        return null;
    }
    public static PushMsgVO buildSystemMsg(String title,String locKey){
        PushMsgVO pushMsgVO = new PushMsgVO();
        pushMsgVO.setLocKey(locKey);
        pushMsgVO.setContent(locKey);
        pushMsgVO.setTitle(title);
        pushMsgVO.setMsgType(MsgTypeEnum.MESSAGE_SYSTEM.getCode());
        return pushMsgVO;
    }
}
