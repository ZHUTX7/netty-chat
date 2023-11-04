//package com.zzz.pro;
//
//
//import com.zzz.pro.enums.MsgTypeEnum;
//import com.zzz.pro.pojo.dto.ChatMsg;
//import com.zzz.pro.controller.vo.ChatMsgVO;
//import com.zzz.pro.utils.Push2;
//import com.zzz.pro.utils.PushUtils;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.Properties;
//import java.util.Scanner;
//import java.util.concurrent.ExecutionException;
//
//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
//public class ProduceTest {
//
//
//
//    @Resource
//    KafkaTemplate<Object,Object> kafkaTemplate ;
//    @Resource
//    Push2 push2;
//
//    @Test
//    public void data2Kafka() throws InterruptedException {
//
//        Scanner scanner = new Scanner(System.in);
//        int i = 1;
////        while (1) {
////            System.out.println("请输入要发送的消息：");
////            String value = scanner.nextLine();
//            // String value = "message";
//            ChatMsg dto = new ChatMsg();
//            dto.setMsgId(new Long(12313544)+i);
//            dto.setMessage("test : "+ "ttt2");
//            dto.setAcceptUserId("10002");
//            dto.setMessageType(4);
//            dto.setSendUserId("10001");
//            dto.setSignFlag(1);
//            dto.setSendTime(new Date());
//
//            kafkaTemplate.send("3ZStudios", dto);
//            Thread.sleep(3000);
//        }
//
//
//    @Test
//    public void test() throws ExecutionException, InterruptedException {
//
//        ChatMsgVO vo = new ChatMsgVO();
//        vo.setSendUserName("test");
//        vo.setMsgType(MsgTypeEnum.MESSAGE_TEXT.getTitle());
//        vo.setContent("收到了回我微信");
//        push2.sendIosMsg("IOS-0d12a2a9ae734d9492b57cc73e605732dd7b71f3fba55b20796a97cc72143038","hello",1);
//
//    }
//
//
//}