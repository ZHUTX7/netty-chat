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
public class Msg2Kafka implements SchedulingConfigurer {

    //每秒执行一次
    private String cron = "0/2 * * * * ? ";

    @Resource
    KafkaTemplate<Object,Object> kafkaTemplate ;

    //同步发送信息
    public SendResult syncSend(ChatMsg chatMsg) throws ExecutionException, InterruptedException {
        log.info("将Message发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg).get();
    }
    //TODO 创建一个缓冲池，将消息放入缓冲池，定时批量发送
    //异步发送信息
    public ListenableFuture<SendResult<Object, Object>> asyncSend(ChatMsg chatMsg) {
        log.info("将Message发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg);
    }
    //kafka批量发送ListChatMsg数据
    public void batchSend(List<ChatMsg> listChatMsg) {
        log.info("将Message发送到kafka");
        kafkaTemplate.send("3ZStudios" ,listChatMsg);
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                () -> {
                    //TODO 批量发送
                   // log.info("执行定时任务");
                },
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //2.1 从数据库获取执行周期
                    //2.2 合法性校验.
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }
}
