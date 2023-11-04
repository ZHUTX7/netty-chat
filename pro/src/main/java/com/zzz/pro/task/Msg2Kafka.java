package com.zzz.pro.task;

import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.SpringUtil;
import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
