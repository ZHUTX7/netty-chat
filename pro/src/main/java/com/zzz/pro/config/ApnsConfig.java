package com.zzz.pro.config;

import com.eatthepath.pushy.apns.*;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class ApnsConfig {

    @Value("${apns.cer-path}")
    private  String cerPath;
    @Value("${apns.cer-password}")
    private  String cerPassword ;
    @Value("${apns.push-env}")
    private  String PUSH_ENV ;
    private ApnsClient apnsClient;

    @Bean(name = "apnsClient")
    @Scope("singleton")
    public  ApnsClient getAPNSConnect() {

        if (apnsClient == null) {
            try {
                EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
                String mode;
                log.info("---------------- APNS推送服务启动成功 ！--------------",PUSH_ENV);
                log.info(">>>>>>>>>>>>>> APNS推送服务启动中，环境采用{}>>>>>>>>>>>>>>",PUSH_ENV);
                if("dev".equals(PUSH_ENV))
                {
                    mode = ApnsClientBuilder.DEVELOPMENT_APNS_HOST;
                }
                else if("prod".equals(PUSH_ENV))
                {
                    mode = ApnsClientBuilder.PRODUCTION_APNS_HOST;
                }
                else {
                    throw new Exception();
                }
                File file = ResourceUtils.getFile(cerPath);
                apnsClient = new ApnsClientBuilder()
                        //APNS生产IP地址
                        .setApnsServer(mode)
                        //P12配置文件时注册密码 "com.mindset.bump"
                        .setClientCredentials(file,cerPassword )
                        .setConcurrentConnections(4)
                        .setEventLoopGroup(eventLoopGroup).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("---------------- APNS推送服务启动成功 ！--------------",PUSH_ENV);
        return apnsClient;
    }
}
