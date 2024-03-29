package com.mindset.ameeno;


import com.mindset.ameeno.netty.WSServer;
import com.mindset.ameeno.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import tk.mybatis.spring.annotation.MapperScan;

//@EnableDiscoveryClient
//@EnableFeignClients
@SpringBootApplication
@MapperScan(basePackages = "com.mindset.ameeno.mapper")
@EnableAspectJAutoProxy
public class ProApplication {
    private static Logger logger = LoggerFactory.getLogger(ProApplication.class);
    @Bean
    public SpringUtil getSpringUtil() {
        return new SpringUtil();
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ProApplication.class, args);
        logger.info(" 启动成功！");
        WSServer.getInstance().start(9999);

    }



}
