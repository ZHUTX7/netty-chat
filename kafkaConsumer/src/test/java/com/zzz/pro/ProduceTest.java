//package com.zzz.pro;
//
//
//import com.zzz.pro.pojo.dto.ChatMsg;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.Properties;
//import java.util.Scanner;
//
//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
//public class ProduceTest {
//    private static Scanner scanner;
//
//    private Producer<String, String> producer;
//    private Properties props;
//
//    public void connectionKafka() throws InterruptedException {
//        props = new Properties();
//        props.put("bootstrap.servers", "192.168.1.9:9092");
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.JsonSerializer");
//        producer = new KafkaProducer<>(props);
//        scanner = new Scanner(System.in);
//        int i = 1;
//        while (true) {
//            System.out.println("请输入要发送的消息：");
//            String value = scanner.nextLine();
//           // String value = "message";
//            ChatMsg dto = new ChatMsg();
//            dto.setMsgId(new Long(12323544)+i);
//            dto.setMessage("test : "+ value);
//            dto.setAcceptUserId("10002");
//            dto.setMessageType(4);
//            dto.setSendUserId("10001");
//            dto.setSignFlag(1);
//            dto.setSendTime(new Date());
//            producer.send(new ProducerRecord<String, ChatMsg>("3ZStudios", dto));
//
//            Thread.sleep(3000);
//        }
//
//
//    }
//
//
//}