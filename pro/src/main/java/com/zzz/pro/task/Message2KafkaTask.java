package com.zzz.pro.task;


import com.zzz.pro.pojo.dto.ChatMsg;
import com.zzz.pro.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;


public class Message2KafkaTask implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(Message2KafkaTask.class);



    //是否异步发送
    private static boolean transferType = false;

    //消息
    private ChatMsg msg;



    public Message2KafkaTask(ChatMsg chatMsg){
        this.msg = chatMsg;
    }

    //同步发送信息
    private SendResult syncSend(ChatMsg chatMsg) throws ExecutionException, InterruptedException {
        KafkaTemplate<Object,Object> kafkaTemplate = (KafkaTemplate<Object,Object> )SpringUtil.getBean("kafkaTemplate");
        logger.info("信息发送到kafka");
        return kafkaTemplate.send("3ZStudios" ,chatMsg).get();
    }
    //异步发送信息
    private ListenableFuture<SendResult<Object, Object>> asyncSend(ChatMsg chatMsg) {
        KafkaTemplate<Object,Object> kafkaTemplate = (KafkaTemplate<Object,Object> )SpringUtil.getBean("kafkaTemplate");
        return kafkaTemplate.send("3ZStudios" ,chatMsg);
    }

    // TODO 信息存储kafka失败
    @Override
    public void run() {
        if(transferType){
            asyncSend(msg);
        }else {
            try {
                syncSend(msg);
            } catch (ExecutionException e) {
                logger.warn("发信消息失败");
                e.printStackTrace();
            } catch (InterruptedException e) {
                logger.warn("发信消息失败");
                e.printStackTrace();
            }
        }

    }
}
