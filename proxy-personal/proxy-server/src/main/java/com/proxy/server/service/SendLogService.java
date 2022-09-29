//package com.proxy.server.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import java.util.Map;
//
///**
// * @author ztx
// * @date 2021-11-29 18:36
// * @description : 发送网关日志到管理平台
// */
//public class SendLogService implements SendMessage{
//    @Override
//    public void sendMessage(Map<String,Object> message) throws Exception {
//
//       // JSON json = new JSONObject(message);
//        if(WsManager.getWsLauncher()!=null){
//            WsManager.getWsLauncher().sendMessage(JSON.toJSONString(message));
//            System.out.println("发送网关日志成功");
//        }else{
//            System.out.println("ws为空");
//        }
//    }
//}
