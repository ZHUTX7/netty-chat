//package com.proxy.client.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.proxy.client.ProxyClient;
//import com.proxy.client.enity.TokenSingleton;
//import com.proxy.client.enity.User;
//import com.proxy.client.enity.UserSingleton;
//import com.proxy.common.util.RpcUtils;
//import org.apache.http.conn.HttpHostConnectException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Map;
//import java.util.Scanner;
//
////   *********************************去除proxy-client上线验证流程 2021/7/12 ***************************
//
///**
// * @author : ztx
// * @version :V1.0
// * @description :  proxy-client 上线
// * @update : 2021/2/23 11:03
// */
//public class Service {
//    private static Logger logger = LoggerFactory.getLogger(ProxyClient.class);
//
//
//
//    public static void login(){
//        int time = 0;
//        ConfigService configService = new ConfigService();
//        Map<String,String> config =  configService.readConfig();
//        String controllerIP = config.get("ControllerIP");
//        String controllerPort = config.get("ControllerPort");
//        logger.info("获取控制器的IP地址:"+controllerIP);
//        while(true) {
//
//            String name = "admin";
//            String password = "admin";
//            String jsonText = "{\n" +
//                    "\t\"user_name\":\""+name+"\",\n" +
//                    "\t\"password\":\""+password+"\",\n" +
//                    "}";
//            JSONObject requestJson = (JSONObject) JSONObject.parse(jsonText);
//            JSONObject responseJson = RpcUtils.callOtherInterface(requestJson, controllerIP, controllerPort, "/verify/login");
//            int errorCode = responseJson.getInteger("errorCode");
//
//            if(errorCode != 1){
//                logger.warn("用户名或密码输入错误。请重新输入\n");
//            }
//            else{
//                User user = JSON.toJavaObject( responseJson.getJSONObject("userInfo"),User.class);
//                UserSingleton.setUser(user);
//                TokenSingleton.setToken(responseJson.getString("token"));
//                TokenSingleton.setClientKey(responseJson.getString("clientKey"));
//                logger.info("SDP控制器认证网关成功 !");
//                break;
//            }
//
//
//
//        }
//        //登录成功后， 获取token , 获取可访问ip地址
//
//
//
//    }
//}
