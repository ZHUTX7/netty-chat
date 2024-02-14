package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.form.ConsumeSKUForm;
import com.mindset.ameeno.enums.*;
import com.mindset.ameeno.mapper.*;
import com.mindset.ameeno.utils.*;

import com.mindset.ameeno.exception.ApiException;

import com.mindset.ameeno.netty.UserChannelMap;
import com.mindset.ameeno.netty.enity.ChatMsg;
import com.mindset.ameeno.pojo.dto.DatingEvaluate;
import com.mindset.ameeno.controller.form.DatingScoreForm;
import com.mindset.ameeno.controller.vo.DatingStatusVO;
import com.mindset.ameeno.controller.vo.PushMsgVO;
import com.mindset.ameeno.pojo.dto.UserPropsBags;
import com.mindset.ameeno.task.Msg2Kafka;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @Author zhutianxiang
 * @Description 约会
 * @Date 2023/8/3 00:15
 * @Version 1.0
 */
@Slf4j
@Service
public class DatingService {
    @Resource
    UserPersonalInfoMapper userPersonalInfoMapper;

    @Resource
    UserMatchMapper userMatchMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    RedisStringUtil redisStringUtil;
    @Resource
    UserDatingMapper userDatingMapper;
    @Resource
    UserFriendsMapper userFriendsMapper;
    @Resource
    FriendsService friendsService;
    @Resource
    DatingEvaluateMapper datingEvaluateMapper;
    @Resource
    PushUtils pushUtils;
    @Resource
    private UserPropsBagsMapper userPropsBagsMapper;
    @Resource
    private SKUService skuService;

    public void acceptDating(String userId, String targetId, Integer status) {

        // 查询对方约会状态
        Integer targetStatus = userDatingMapper.queryDatingStatus(targetId , userId);
        if (ObjectUtils.isEmpty(targetStatus)) {
            return;
        }
        String dvId = redisStringUtil.get(RedisKeyEnum.USER_DEVICE_ID.getCode() + targetId);
        //我到了约会地点  status = 5
        if (status == RelEnum.DATING_WAIT_ARRIVE.getCode()) {
            //对方也到目标地点
            if (targetStatus == RelEnum.DATING_WAIT_ARRIVE.getCode()) {
                userDatingMapper.updateBothDatingStatus(userId, targetId, RelEnum.DATING_BOTH_ARRIVE.getCode(), RelEnum.DATING_BOTH_ARRIVE.getCode());
                PushMsgVO vo = PushMsgVO.buildSystemMsg( "Ammeno", IOSLocKeyEnum.NOTI_DATING_BOTH_ARRIVE_KEY.getCode());
                pushUtils.pushMsg(vo,dvId);
                return;

            }
            //仅自己到达
            PushMsgVO vo = PushMsgVO.buildSystemMsg( "Ammeno",IOSLocKeyEnum.NOTI_DATING_MATCHER_ARRIV.getCode());
            pushUtils.pushMsg(vo,dvId);
            userDatingMapper.updateBothDatingStatus(userId, targetId, RelEnum.DATING_WAIT_ARRIVE.getCode(), RelEnum.DATING_I_INBOARD.getCode());

            return;
        }
        // 1. 对方已经同意见面
        if (targetStatus==1) {
            //更新自己dating状态
            userDatingMapper.updateStatusAndTime(userId, targetId, RelEnum.DATING_START.getCode(),new Date());
            PushMsgVO vo = PushMsgVO.buildSystemMsg( "Ammeno", IOSLocKeyEnum.NOTI_DATING_MSG_KEY.getCode());
        } else {
            //对方没同意
            //对方的约会状态改为2 自己的改为1
            userDatingMapper.updateBothDatingStatus(userId, targetId, RelEnum.DATING_WAIT_ACCEPT.getCode(), RelEnum.DATING_WAIT_INVITE_CORRECT.getCode());
            PushMsgVO vo = PushMsgVO.buildSystemMsg( "Ammeno","✨✨✨ 你收到一个约会邀请💌 ✨✨✨ ");
            pushUtils.pushMsg(vo,dvId);
        }
        //TODO 添加约会记录
    }

    public void datingDelay(String userId, String targetId, Integer status) {
        // 查询对方约会状态
        Integer targetStatus = userDatingMapper.queryDatingStatus(targetId , userId);
        //如果对方状态也是过期了，说明对方已经知晓约会过期
        //TODO 会员可以发起重新约会，或者延时
        if (ObjectUtils.isEmpty(targetStatus) || targetStatus == RelEnum.DATING_INCORRECT_DELAY.getCode()) {
            userDatingMapper.deleteBothDating(userId, targetId);
            userMatchMapper.deleteRelByUid(userId, targetId);
            return;
        }
        userDatingMapper.updateMyselfStatus(userId, targetId, RelEnum.DATING_INCORRECT_DELAY.getCode());
        userMatchMapper.updateMyMatchState(userId, targetId, RelEnum.MATCH_I_CANCEL.getCode());
        //TODO 添加约会记录
    }

    @Transactional
    public void completeDating(String userId, DatingScoreForm form) {
        userDatingMapper.updateMyStatus(userId, RelEnum.DATING_FINISHED.getCode());
        datingEvaluate(userId,form);
        //TODO 添加约会记录
    }


    public DatingStatusVO queryDatingStatus(String userId, String targetId) {
        // 查询约会状态
        Integer status = userDatingMapper.queryDatingStatus(userId , targetId);

        DatingStatusVO vo = new DatingStatusVO();
        String msg =  RelEnum.getTitleByCode(status);
        vo.setStatus(status);
        vo.setMsg(msg);
        return vo;

    }


    //评分
    public void datingEvaluate(String userId,DatingScoreForm form){
        String did = CRCUtil.crc32Hex(userId+form.getTargetId());
        DatingEvaluate datingEvaluate = new DatingEvaluate();
        datingEvaluate.setId(UUID.randomUUID().toString());
        datingEvaluate.setDatingId(did);
        datingEvaluate.setScore(form.getScore());
        datingEvaluate.setEvaluate(form.getEvaluate());
        datingEvaluate.setEvaluateTime(new Date());
        datingEvaluate.setUserId(userId);
        datingEvaluate.setTargetId(form.getTargetId());
        //评价落库
        datingEvaluateMapper.insertDatingEvaluate(datingEvaluate);
        DatingEvaluate targetEvaluate = datingEvaluateMapper.queryDatingEvaluate(form.getTargetId(),userId);

        //双方评分大于3 ,互相添加好友
        if(!ObjectUtils.isEmpty(targetEvaluate)){
            double score = targetEvaluate.getScore();
            if(score>=3 && form.getScore()>=3){
                //添加到好友列表
                friendsService.makeFriendsRel(userId, form.getTargetId());
            }
        }

    }

    //查询自己给别人的约会评分
    public Object queryDatingEvaluate(String userId,String targetId){
        return datingEvaluateMapper.queryDatingEvaluate(userId,targetId);
    }


    //添加约会记录
    //1 - 约会完成
    //1.1 约会正常完成
    //1.2 约会过期
    //2 - 约会中
    //3 - 约会未开始
    @Transactional
    public void datingSkip(String userId, String targetId) {
        //1.判断sku


        friendsService.makeFriendsRel(userId, targetId);
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("< 提示：由于附近没有合适的见面地点，展维赠送您一次[跳过见面]服务 🎉 >");
        chatMsg.setMsgId(chatMsg.getMsgId()+"1");
        chatMsg.setMsgType(MsgTypeEnum.MESSAGE_ALERT.getCode());
        chatMsg.setReceiverId(userId);
        chatMsg.setSenderId(targetId);
        Channel receiveChannel = UserChannelMap.getInstance().get(targetId);
        int sign = 0;
        if(receiveChannel != null){
            receiveChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(chatMsg)));
            sign =1;
        }
        //信息入库
        chatMsg.setReceiverId(targetId);
        chatMsg.setSenderId(userId);
        com.mindset.ameeno.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
        dto.setSignFlag(sign);
        Msg2Kafka msg2Kafka  = (Msg2Kafka) SpringUtil.getBean("msg2Kafka");
        msg2Kafka.asyncSend(dto);
    }

    @Transactional
    public void datingSkipBySKU(String userId, String targetId) {
        //1.判断sku
        skuService.consumeProduct(userId,targetId, "6", 1);
        friendsService.makeFriendsRel(userId, targetId);

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("< 提示：对方刚刚使用跳过见面道具，您直接与对方成为好友～ >");
        chatMsg.setMsgId(chatMsg.getMsgId()+"1");
        chatMsg.setMsgType(MsgTypeEnum.MESSAGE_ALERT.getCode());
        chatMsg.setReceiverId(userId);
        chatMsg.setSenderId(targetId);
        Channel receiveChannel = UserChannelMap.getInstance().get(targetId);
        int sign = 0;
        if(receiveChannel != null){
            receiveChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(chatMsg)));
            sign =1;
        }
        //信息入库
        chatMsg.setReceiverId(targetId);
        chatMsg.setSenderId(userId);
        com.mindset.ameeno.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
        dto.setSignFlag(sign);
        Msg2Kafka msg2Kafka  = (Msg2Kafka) SpringUtil.getBean("msg2Kafka");
        msg2Kafka.asyncSend(dto);
    }
}
