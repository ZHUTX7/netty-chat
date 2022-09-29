//package com.proxy.server.unitTest;
//
//import com.proxy.common.entity.server.ProxyRealServer;
//import com.proxy.common.protocol.RedisKeyNameConfig;
//import com.proxy.server.dao.GatewayRedisDAO;
//import com.proxy.server.dto.GatewayRouteDTO;
//import com.proxy.server.util.RedisPoolDao;
//import redis.clients.jedis.Jedis;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author : ztx
// * @version :V1.0
// * @description : 单元测试工具 ， 在Redis中添加数据
// * @update : 2021/2/3 20:34
// */
//public class RedisTest {
//
//    public static void addProxy2Redis(){
//        List<ProxyRealServer> routeDtoList = new ArrayList<>();
//        ProxyRealServer proxyRealServer = new ProxyRealServer();
//        proxyRealServer.setClientKey("client1000001");
//        proxyRealServer.setRealHost("192.168.3.199");
//        proxyRealServer.setRealHostPort(8008);
//        proxyRealServer.setDescription("测试");
//        proxyRealServer.setServerPort(9004);
//        proxyRealServer.setProxyType(1);//tcp 1
//        routeDtoList.add(proxyRealServer);
//        GatewayRedisDAO.saveGatewayRoute(new GatewayRouteDTO(1000001,"ztxToken5","127.0.0.1","zzz",routeDtoList));
//        System.out.println("finished");
//    }
//
//
//
//
//    //测试
//    public static void main(String[] args) {
//        //addProxy2Redis();
//    Jedis redis = RedisPoolDao.getRedisPool().getResource();
//        //System.out.println(redis.srem("192.168.3.199:8008","3"));
//        //System.out.println(redis.getSet("192.168.199:8080",5+""));
//    }
//
//}
