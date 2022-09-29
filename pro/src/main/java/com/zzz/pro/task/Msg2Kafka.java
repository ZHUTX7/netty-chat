package com.zzz.pro.task;

import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.SpringUtil;
import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class Msg2Kafka{

    @Resource
    KafkaTemplate<Object,Object> kafkaTemplate ;

    //同步发送信息
    public SendResult syncSend(ChatMsg chatMsg) throws ExecutionException, InterruptedException {
        log.info("将Message发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg).get();
    }
    //异步发送信息
    public ListenableFuture<SendResult<Object, Object>> asyncSend(ChatMsg chatMsg) {
        log.info("将Message发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg);
    }

}
