package com.zzz.pro;


import com.zzz.pro.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;

@SpringBootApplication
@MapperScan(basePackages = "com.zzz.pro.mapper")
public class KafkaConsumer {
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumer.class, args);
        logger.info(" 启动成功！");

    }



}
