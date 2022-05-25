package com.zzz.pro.task;

import io.netty.channel.DefaultEventLoopGroup;
import org.springframework.stereotype.Component;


public class TaskExecutor{

    private static DefaultEventLoopGroup defaultEventLoopGroup
            = new DefaultEventLoopGroup(5);

    public static void submit(Message2KafkaTask task){
        if(defaultEventLoopGroup == null){
            defaultEventLoopGroup =new DefaultEventLoopGroup(5);
        }
        defaultEventLoopGroup.submit(task);
    }

}
