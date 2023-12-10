package com.zzz.pro.utils;


import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.eatthepath.pushy.apns.*;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.notnoop.apns.PayloadBuilder;
import com.zzz.pro.enums.MsgTypeEnum;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.controller.vo.PushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Slf4j
@Service
public class PushUtils {

    @Resource(name = "apnsClient")
    private ApnsClient apnsClient;
    @Value("${apns.topic}")
    private String topic;
    @Resource
    private RedisStringUtil redisStringUtil;

    public  void pushMsg(PushMsgVO vo , String deviceId) {

        if (StringUtils.isEmpty(deviceId) || vo == null) {
            return;
        }
        String type = deviceId.substring(0, 4);
        int  badge  = (int) redisStringUtil.incr(RedisKeyEnum.USER_UNREAD_MSG_COUNT.getCode()+deviceId,1);

        switch (type) {
            case "IOS-":
                deviceId = deviceId.substring(4, deviceId.length());
                pushIosMsg(vo,deviceId,badge);
                break;
            case "ANDROID-":
                deviceId = deviceId.substring(8, deviceId.length());
                pushAndroidMsg(vo,deviceId,badge);
                break;
            default:
                return;
        }


    }

    private PushNotificationResponse<SimpleApnsPushNotification>  pushIosMsg(PushMsgVO vo , String deviceId, int badge){
        if(StringUtils.isEmpty(deviceId) || vo.getMsgType()==null){
            log.error("pushIosMsg - deviceId or msType 参数为空");
            return null;
        }
        //用户级别Text推送
        if (vo.getMsgType()==MsgTypeEnum.MESSAGE_TEXT.getCode()){
            return   pushChatMsg(vo,deviceId,badge);
        }
        //系统级别推送
        return  pushSystemMsg(vo,deviceId,badge);


    };
    private PushNotificationResponse<SimpleApnsPushNotification> pushChatMsg(PushMsgVO vo , String deviceId,int badge) {
        Date invalidationTime = new Date(System.currentTimeMillis() + 60 * 1000L);
        Instant instant = invalidationTime.toInstant();
        // 构造一个APNs的推送消息实体
        PayloadBuilder payloadBuilder = PayloadBuilder.newPayload();
        payloadBuilder.badge(badge);
        payloadBuilder.alertBody(vo.getContent());
        payloadBuilder.sound("default");
        payloadBuilder.alertTitle(vo.getSendUserName());
        //-------- key -------------
        payloadBuilder.customField("sendUserId",vo.getSendUserId());
        payloadBuilder.customField("msgType",vo.getMsgType());
        payloadBuilder.customField("sendUserName",vo.getSendUserName());
        payloadBuilder.instantDeliveryOrSilentNotification();
        String payload = payloadBuilder.build();
        SimpleApnsPushNotification msg = new SimpleApnsPushNotification(deviceId, topic, payload, instant, DeliveryPriority.IMMEDIATE, PushType.ALERT);
        // 开始推送
        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> future = apnsClient.sendNotification(msg);
        PushNotificationResponse<SimpleApnsPushNotification> response = null;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void clearIosBadge(String deviceId){
        if(StringUtils.isEmpty(deviceId)){
            return ;
        }
        redisStringUtil.del(RedisKeyEnum.USER_UNREAD_MSG_COUNT.getCode()+deviceId) ;
        Date invalidationTime = new Date(System.currentTimeMillis() + 60 * 1000L);
        Instant instant = invalidationTime.toInstant();
        // 构造一个APNs的推送消息实体
        PayloadBuilder payloadBuilder = PayloadBuilder.newPayload();
        payloadBuilder.badge(0);
        //-------- key -------------
        String payload = payloadBuilder.build();
        SimpleApnsPushNotification msg = new SimpleApnsPushNotification(deviceId, topic, payload, instant, DeliveryPriority.IMMEDIATE, PushType.ALERT);
        // 开始推送
        apnsClient.sendNotification(msg);

    }

    private void pushAndroidMsg(PushMsgVO vo,String deviceId,int badge){
        //TODO

    };

    private PushNotificationResponse<SimpleApnsPushNotification> pushSystemMsg(PushMsgVO vo,String deviceId,int badge){
        Date invalidationTime = new Date(System.currentTimeMillis() + 60 * 1000L);
        Instant instant = invalidationTime.toInstant();
        // 构造一个APNs的推送消息实体
        PayloadBuilder payloadBuilder = PayloadBuilder.newPayload();
        payloadBuilder.badge(badge);
        payloadBuilder.alertBody(vo.getContent());
        payloadBuilder.sound("default");
        payloadBuilder.alertTitle(vo.getTitle());
        payloadBuilder.localizedKey("NOTI_MATCHING_MSG_KEY");
        //-------- key -------------
        String payload = payloadBuilder.build();
        SimpleApnsPushNotification msg = new SimpleApnsPushNotification(deviceId, topic, payload, instant, DeliveryPriority.IMMEDIATE, PushType.ALERT);
        // 开始推送
        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> future = apnsClient.sendNotification(msg);
        PushNotificationResponse<SimpleApnsPushNotification> response = null;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    };

}