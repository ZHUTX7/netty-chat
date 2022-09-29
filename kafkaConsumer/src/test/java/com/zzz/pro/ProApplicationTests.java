//package com.zzz.pro;
//import com.zzz.pro.mapper.ChatMsgMapper;
//import com.zzz.pro.pojo.dto.ChatMsg;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.util.Date;
//
//@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
//public class ProApplicationTests {
//
//    @Resource
//    private ChatMsgMapper chatMsgMapper;
//
//    @Test
//    public   void test2Mysql() {
//
//        for(int i = 0;i<5;i++){
//            ChatMsg dto = new ChatMsg();
//            dto.setMsgId(new Long(12323544)+i);
//            dto.setMessage("test : "+i);
//            dto.setAcceptUserId("test");
//            dto.setMessageType(4);
//            dto.setSendUserId("test");
//            dto.setSignFlag(1);
//            dto.setSendTime(new Date());
//            chatMsgMapper.insert(dto);
//        }
//
//    }
//
//    @Test
//    public   void delData() {
//        for(int i = 0;i<5;i++) {
//            ChatMsg dto = new ChatMsg();
//            dto.setMsgId(new Long(12323544)+i);
//            chatMsgMapper.delete(dto);
//        }
//}
//}
