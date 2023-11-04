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
import java.util.List;


@Component
public class Msg2Kafka {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ChatMsgMapper chatMsgMapper;

    @KafkaListener(topics = "3ZStudios", groupId = "consumer-group-" + "3ZStudios", containerFactory = "batchFactory")
    public void onMessages(List<ChatMsg> chatMsgList, Acknowledgment acknowledgment) {
        try {
            chatMsgMapper.insertList(chatMsgList);
            // 手动提交ack，表示消息已成功处理
            acknowledgment.acknowledge();
            logger.info("批量成功");
        } catch (Exception e) {
            // 处理消息过程中发生错误，可以选择记录日志或者进行其他处理
            logger.error("批量失败", e.getMessage());
        }
    }

}
