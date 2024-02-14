package com.mindset.ameeno.task;

import com.mindset.ameeno.pojo.dto.ChatMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

@Component
@Slf4j
//开启定时任务
@EnableScheduling
public class Msg2Kafka  {
    @Resource
    KafkaTemplate<Object,Object> kafkaTemplate ;
    //异步发送信息
    public ListenableFuture<SendResult<Object, Object>> asyncSend(ChatMsg chatMsg) {
        log.info("将Message发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg);
    }

}
