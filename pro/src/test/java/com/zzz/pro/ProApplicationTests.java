package com.zzz.pro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.zzz.pro.enums.MsgTypeEnum;
import com.zzz.pro.filter.SensitiveFilter;
import com.zzz.pro.mapper.UserPersonalInfoMapper;
import com.zzz.pro.netty.enity.DataContent;
import com.zzz.pro.netty.enity.SystemMsg;
import com.zzz.pro.controller.vo.ChatMsgVO;
import com.zzz.pro.controller.vo.PushMsgVO;
import com.zzz.pro.controller.vo.UserVO;
import com.zzz.pro.service.BloomFilterService;
import com.zzz.pro.service.ChatMsgService;

import com.zzz.pro.service.MapService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.JsonUtils;
import com.zzz.pro.utils.PushUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@Slf4j
class ProApplicationTests {

    @Resource
    UserPersonalInfoMapper mapper;
    @Resource
    ChatMsgService chatMsgService;
    @Resource
    PushUtils pushUtils;
    @Resource
    SensitiveFilter sensitiveFilter;
    @Resource
    MapService mapService;
    @Resource
    BloomFilterService bloomFilterService;





    @Test
    void distanceTest(){
        System.out.println("距离： "+ mapService.getDistance("b2594dbb","f2fde0db"));
    }

    @Test
    void bloomTest(){
        bloomFilterService.add("qqq","123");
        System.out.println(bloomFilterService.mightContain("qqq","123")); ;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void push() throws ExecutionException, InterruptedException {

        PushMsgVO vo = new PushMsgVO();
        vo.setSendUserName("张三");
        vo.setContent("hello");
        vo.setSendUserId("aaaa");
        vo.setMsgType(MsgTypeEnum.MESSAGE_SYSTEM.getCode());
        pushUtils.pushMsg(vo,"IOS-e796792d4bdedc06c15c11bbfcc95e02b4972b2e82052d7193371a74bc808da0");
       // pushUtils.clearIosBadge("b913782ca6643b94cb78bbbc10b02ce2c5bb903627466844865e068b32ed83d9",3);
    }

    @Test
    void t3(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyUm9sZSI6IjEiLCJ1c2VySWQiOiIwMDA2NGQ0YyIsImRldmljZUlkIjoiSU9TLTcyZWQ5OWVkZmFlNTE3YjE3MTkzNmYzMDQ2OWFiMWRlMWE3YTVlNGJiOGQzYjdmMDZkYmI2MmRjZDAzZTdlZDcifQ.yTs8TStkOjgTfhx2B5hMvp9G-4bVMJNb6KJ12-P_jc0";
        String userId =  JWTUtils.getClaim(token,"userId");
        System.out.printf("userId = " + userId);
    }

    @Test
    void tt(){
        UserVO vo =  mapper.queryUserVO("10002");
        System.out.println(vo.getUserId());
        ;
    }
    @Test
    void selectVO(){
         ChatMsgVO vo  =  chatMsgService.getUnSignMsg("10002").get(0);
        System.out.println(vo.getSendUserId());
        System.out.println(vo.getMessage());
    }
    @Test
    void testJson() {
        String s = "\"systemMsg\" : [\n" +
                "    \"1097653562357452800\",\n" +
                "    \"1097653680011874304\",\n" +
                "    \"1097653710840008704\",\n" +
                "    \"1097653744927117312\",\n" +
                "    \"1097653760508956672\",\n" +
                "    \"1097653775717502976\",\n" +
                "    \"1097653787927121920\",\n" +
                "    \"1097653803676733440\",\n" +
                "    \"1097654212495544320\",\n" +
                "    \"1097677301094682624\"\n" +
                "  ],\n" +
                "  \"action\" : 3,\n" +
                "  \"token\" : \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyUm9sZSI6IjEiLCJ1c2VySWQiOiJhMDkzNDlhZCIsImRldmljZUlkIjoiSU9TLTQ4OTY2YjgxMTM3YzljMDc3ZWRhMjE4MjE2YjExNzgyZTEyNGUxOGRiYzYyMmNlMTQ5M2JkMmM\"[truncated 62 chars]; line: 2, column: 14] (through reference chain: com.zzz.pro.netty.enity.DataContent[\"expand\"])\n" +
                "";
        DataContent dataContent =  JsonUtils.jsonToPojo(s, DataContent.class);
        DataContent data = new DataContent();
        data.setAction(1);
        data.setToken("123");
        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setMsgList(List.of("1","2","3"));
        data.setSystemMsg(systemMsg);
        System.out.printf(JsonUtils.objectToJson(data));
//        List<String> list = dataContent.getSystemMsg().getMsgList();
//        for (String s1 : list) {
//            System.out.println(s1);
//        }
    }

}
