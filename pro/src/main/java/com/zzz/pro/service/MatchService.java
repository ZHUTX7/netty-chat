package com.zzz.pro.service;

import com.zzz.pro.enums.*;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.*;
import com.zzz.pro.netty.UserChannelMap;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.controller.vo.*;
import com.zzz.pro.task.Msg2Kafka;
import com.zzz.pro.utils.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class MatchService {

    @Resource
    UserPersonalInfoMapper userPersonalInfoMapper;
    @Resource
    PushUtils pushUtils;
    @Resource
    UserMatchMapper userMatchMapper;
    @Resource
    RedisStringUtil redisStringUtil;
    @Resource
    UserDatingMapper userDatingMapper;
    @Resource
    UserFriendsMapper userFriendsMapper;
    @Resource
    UserVOCache userVOCache;
    @Resource
    MapService mapService;
    @Resource
    private UserPropsBagsMapper userPropsBagsMapper;
    @Resource
    RecommendPoolService recommendPoolService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void match(String userId) {
        //确认该用户是否已经有建立匹配关系的对象
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId(userId);

        if (redisStringUtil.hget(RedisKeyEnum.BOYS_WAITING_POOL.getCode(), user.getUserId()) != null ||
                redisStringUtil.hget(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(), user.getUserId()) != null) {
            return;
        }

        if (userMatchMapper.queryMatchUserCount(userId) > 0) {
            log.error("userId:{} 已经有匹配对象",userId);
            throw new ApiException(200, "已经有匹配对象啦～ 好好聊吧");
        }


        UserPersonalInfo userPersonalInfo = userPersonalInfoMapper.selectByPrimaryKey(userId);
        UserProfileVO userProfileVO = new UserProfileVO();

        BeanUtils.copyProperties(userPersonalInfo, userProfileVO);
        String sex = userPersonalInfo.getUserSex();
        //进入Redis 匹配用户池
        if (sex.equals(SexEnum.MALE.getCode())) {
            redisStringUtil.hset(RedisKeyEnum.BOYS_WAITING_POOL.getCode(), user.getUserId(), "on");
        } else if (sex.equals(SexEnum.FEMALE.getCode())) {
            redisStringUtil.hset(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(), user.getUserId(), "on");
        }

    }


    public void stopMatch(String userId) {
        redisStringUtil.hdel(RedisKeyEnum.BOYS_WAITING_POOL.getCode(), userId);
        redisStringUtil.hdel(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(), userId);
        log.info("用户ID{}停止匹配", userId);
    }


    public Map<String, String> queryMatchStatus(String userId) {
        Map<String, String> map = new HashMap<>();
        if (redisStringUtil.hHasKey(RedisKeyEnum.BOYS_WAITING_POOL.getCode(), userId) ||
                redisStringUtil.hHasKey(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(), userId)) {
            map.put("matchStatus", "on");
        } else {
            map.put("matchStatus", "off");
        }
        return map;
    }

//    public List<UserProfileVO> pushMatchUserList2(UserFilterForm userFilterForm, String userId) {
//        Set<String>  set = recommendPoolService.getDifference(userId);
//
//
//    }

    //解除匹配
    @Transactional(propagation = Propagation.REQUIRED)
    public void delMatch(UserMatch userMatch) {
        try {
            //1. 解除匹配，修改双方匹配状态
            //查询对方状态 ,对方是否已经把我删了
            int state =  userMatchMapper.queryMatchState(userMatch.getMatchUserId(),userMatch.getMyUserId());
            //对方删了我，进入双删
            if(state == RelEnum.MATCH_I_CANCEL.getCode()){
               userMatchMapper.deleteRelByUid(userMatch.getMyUserId(),userMatch.getMatchUserId());
               userDatingMapper.deleteBothDating(userMatch.getMyUserId(),userMatch.getMatchUserId());
               return;
            }

            //对方没删我，进入单删
            userMatchMapper.updateBothMatchState(userMatch.getMyUserId(), userMatch.getMatchUserId(),
               RelEnum.MATCH_I_CANCEL.getCode(), RelEnum.MATCH_U_CANCEL.getCode());

            //2.解决约会，修改约会状态
            UserDating  dating =  userDatingMapper.queryDating(userMatch.getMyUserId(), userMatch.getMatchUserId());
            if(dating != null){
                //如果约会状态在进行中 ， 扣除用户信用分TODO
                if(dating.getStatus().equals(RelEnum.DATING_START.getCode())){
                    //TODO 扣除用户信用分
                    //TODO 通知用户
                }
                //删除约会
                userDatingMapper.updateBothDatingStatus(userMatch.getMyUserId(), userMatch.getMatchUserId(),
                        RelEnum.DATING_I_CANCEL.getCode(),RelEnum.DATING_U_CANCEL.getCode());
            }

        } catch (Exception e) {
            throw new ApiException(ResultEnum.FAILED.getCode(), "解除失败～");
        }

    }


    public MatchUserVO getMatchPerson(String userId) {
        //业务需求 1：1匹配
        List<UserMatch> list = userMatchMapper.queryMatchInfo(userId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        MatchUserVO vo = new MatchUserVO();
        String matchUserId = list.get(0).getMatchUserId();
        UserVO uvo =  userVOCache.getUserVO(matchUserId);
        BeanUtils.copyProperties(uvo,vo);
        vo.setDistance(mapService.getDistance(userId, matchUserId));
        vo.setStatus(list.get(0).getActiveState());
        Date date = userMatchMapper.queryMatchTime(userId,matchUserId);
        UserDating userDating =  userDatingMapper.queryDating(userId,matchUserId);
        vo.setMatchTime(date.getTime());
        if(userDating == null){
            return vo;
        }
        vo.setStatus(userDating.getStatus());
        vo.setDatingStartTime(userDating.getDatingTime().getTime());
        return vo;
    }


    @Transactional
    public SysJSONResult boostMatch(String userId, String targetId) {
        //1.判断是否为单向选择(我是否被他喜欢
        String crc = CRCUtil.crc32Hex(targetId + userId);
        String me =  redisStringUtil.get(RedisKeyEnum.MATCH_SELECTED_POOL.getCode() + crc);
        String dvId =  redisStringUtil.get(RedisKeyEnum.USER_DEVICE_ID.getCode() + targetId);

        if (StringUtils.isEmpty(me)) {
            //说明对方没有选过我
            crc = CRCUtil.crc32Hex(userId + targetId);
            redisStringUtil.set(RedisKeyEnum.MATCH_SELECTED_POOL.getCode() + crc, "1");
            recommendPoolService.addSelectPool(userId,targetId);
            PushMsgVO vo =  PushMsgVO.buildSystemMsg("Ameeno", IOSLocKeyEnum.NOTI_MATCHING_MSG_KEY.getCode());
            pushUtils.pushMsg(vo,dvId);
            return ResultVOUtil.error(201, null);
        }
        //2.判断对方有匹配对象  //有对象的从待匹配池中删除，可以省去此步骤
        if (userMatchMapper.queryMatchUserCount(targetId) > 0) {
            return ResultVOUtil.error(ResultEnum.DUPLICATE_DATA.getCode(), "对方已经有匹配对象～");
        }

        Date date = new Date();
        UserMatch userMatch = new UserMatch();
        userMatch.setMyUserId(userId);
        userMatch.setMatchUserId(targetId);
        userMatch.setActiveState(RelEnum.MATCH_OK.getCode());
        userMatch.setMatchTime(date);
        UserMatch userMatch2 = new UserMatch();
        userMatch2.setMyUserId(targetId);
        userMatch2.setMatchUserId(userId);
        userMatch2.setActiveState(RelEnum.MATCH_OK.getCode());
        userMatch2.setMatchTime(date);

        userMatchMapper.insert(userMatch);
        userMatchMapper.insert(userMatch2);
        redisStringUtil.del(RedisKeyEnum.MATCH_SELECTED_POOL.getCode() + crc);
        List<UserDating> list = new ArrayList<>();
        UserDating dating = new UserDating();
        dating.setUserId(userId);
        dating.setUserTargetId(targetId);
        dating.setStatus(RelEnum.DATING_READY.getCode());
        Date now = new Date();
        dating.setDatingTime(now);
        list.add(dating);

        UserDating dating2 = new UserDating();
        dating2.setUserId(targetId);
        dating2.setUserTargetId(userId);
        dating2.setStatus(RelEnum.DATING_READY.getCode());
        dating2.setDatingTime(now);
        list.add(dating2);

        userDatingMapper.insertList(list);
        PushMsgVO vo =  PushMsgVO.buildSystemMsg("Ameeno", IOSLocKeyEnum.NOTI_MATCHED_MSG_KEY.getCode());
        pushUtils.pushMsg(vo,dvId);
        return ResultVOUtil.success("恭喜，匹配成功～", null);
    }


    public void unBoostMatch(String userId, String targetId) {
        recommendPoolService.addBlackPool(userId,targetId);
    }



   public void chatDelay(String userId,String targetId){
       UserPropsBags consumerSKU =   userPropsBagsMapper.selectOneSKU(userId,"INFINITY_CHAT");
       if(ObjectUtils.isEmpty(consumerSKU) || consumerSKU.getProductCount() <= 0){
           throw new ApiException(ResultEnum.PROPS_NOT_ENOUGH.getCode(), "您没有该道具");
       }
       consumerSKU.setProductCount(consumerSKU.getProductCount() - 1);
       userPropsBagsMapper.updateByPrimaryKeySelective(consumerSKU);
       com.zzz.pro.netty.enity.ChatMsg chatMsg = new com.zzz.pro.netty.enity.ChatMsg();
       chatMsg.setMsg("< 提示：对方刚刚使用聊天延时道具～ >");
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
       com.zzz.pro.pojo.dto.ChatMsg dto =  BeanCopy.copy(chatMsg);
       dto.setSignFlag(sign);
       Msg2Kafka msg2Kafka  = (Msg2Kafka) SpringUtil.getBean("msg2Kafka");
       msg2Kafka.asyncSend(dto);
   }

}
