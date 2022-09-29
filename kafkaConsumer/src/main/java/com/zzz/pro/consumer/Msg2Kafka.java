package com.zzz.pro.consumer;


import com.zzz.pro.mapper.ChatMsgMapper;
import com.zzz.pro.pojo.dto.ChatMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class Msg2Kafka {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ChatMsgMapper chatMsgMapper;


    @KafkaListener(topics = "3ZStudios",
            groupId = "consumer-group-" + "3ZStudios")
    public void onMessage(ChatMsg chatMsg) {
        System.out.println(chatMsg.getMessageType());
        chatMsgMapper.insertMsg(chatMsg);
        //手动提交, Acknowledgment ack
        //TODO 加上ACK就类型转换报错
       // logger.info("Kafka to Mysql 执行状态{},消息内容{}", chatMsg.getMessage());
    }
}
