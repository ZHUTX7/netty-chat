package com.proxy.server.dao;

import com.proxy.common.entity.server.ProxyRealServer;
import com.proxy.common.protocol.RedisKeyNameConfig;
import com.proxy.server.dto.GatewayRouteDTO;
import com.proxy.server.util.RedisObjUtil;
import com.proxy.server.util.RedisPoolDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @description :  Redis - 网关DAO层 - 对应的HashName
 * @update : 2021/2/1 19:21
 */
public class GatewayRedisDAO {
    private static final Logger log = LoggerFactory.getLogger("UserRedisDAO");


    public static boolean  saveGatewayRoute(GatewayRouteDTO gatewayRouteDto){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            // RedisObjUtil.setUser();
            RedisObjUtil.setUserObj(jedis, RedisKeyNameConfig.getRouteConfig(), gatewayRouteDto.getProxy_client_key(), gatewayRouteDto);
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }

    public static boolean removeClientRoute(String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            //清空原有路由信息表
            jedis.del(RedisKeyNameConfig.getTcpRout(clientKey));
            jedis.del(RedisKeyNameConfig.getUdpRout(clientKey));
            //TODO：此处还需移除HTTP
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }

//    public static boolean addRouteList(List<ProxyRealServer> list){
//        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
//        Jedis jedis = jedisPool.getResource();
//        boolean result = false;
//
//        if(list.size() < 1)
//            return false;
//
//        String clientKey = list.get(0).getClientKey();
//
//        try{
//            //先清空原有路由信息表
//            jedis.del(RedisKeyNameConfig.getTcpRout(clientKey));
//            jedis.del(RedisKeyNameConfig.getUdpRout(clientKey));
//            //再加载新的路由信息表
//            for(ProxyRealServer e : list){
//                if(e.getProxyType() == 1){
//                    jedis.hset(RedisKeyNameConfig.getTcpRout(clientKey),e.getServerPort()+"",e.getAddress());
//                }
//                else if(e.getProxyType() == 3){
//                    jedis.hset(RedisKeyNameConfig.getUdpRout(clientKey),e.getServerPort()+"",e.getAddress());
//                }
//                //TODO : HTTP路由信息没有加载
//                else
//                    return false;
//            }
//
//            result = true;
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally {
//            if (jedis != null)
//                jedis.close();
//        }
//        return result;
//    }


    public static GatewayRouteDTO queryGatewayRout(String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        GatewayRouteDTO gatewayRouteDto =  null;
        try{
            // object =  ;
            gatewayRouteDto =   RedisObjUtil.getProxyClient(jedis,RedisKeyNameConfig.getRouteConfig(),clientKey);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }

        return gatewayRouteDto;
    }

    public static List<GatewayRouteDTO> queryGatewayList(){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        List<GatewayRouteDTO> userList = new ArrayList<>();

        try{
            Map<byte[], byte[]> map =  jedis.hgetAll(RedisKeyNameConfig.getRouteConfig().getBytes());
            map.forEach((k,v)->{
                ByteArrayInputStream bis = new ByteArrayInputStream(v);
                ObjectInputStream ois = null;
                GatewayRouteDTO GatewayRouteDto = null;
                try {
                    ois = new ObjectInputStream(bis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    GatewayRouteDto =(GatewayRouteDTO) ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(GatewayRouteDto.getGateway_id());
            });


        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return userList;
    }



    public static boolean addTcpRoute(int port,String ipAndPort,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            jedis.hset(RedisKeyNameConfig.getTcpRout(clientKey),port+"",ipAndPort);
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }
    public static String queryTcpRoute(int port,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        String ipAndPort =null;
        try{
            // RedisObjUtil.setUser();
            ipAndPort =  jedis.hget(RedisKeyNameConfig.getTcpRout(clientKey),port+"");

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return ipAndPort;
    }


    public static boolean deleteTcpRoute(int port,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            // RedisObjUtil.setUser();
            jedis.hdel(RedisKeyNameConfig.getTcpRout(clientKey),port+"");
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }

    public static boolean addUdpRoute(int port,String ipAndPort,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            // RedisObjUtil.setUser();

            jedis.hset(RedisKeyNameConfig.getUdpRout(clientKey),port+"",ipAndPort);
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }
    public static String queryUdpRoute(int port,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        String ipAndPort =null;
        try{
            // RedisObjUtil.setUser();
            ipAndPort =  jedis.hget(RedisKeyNameConfig.getUdpRout(clientKey),port+"");

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return ipAndPort;
    }
    public static boolean deleteUdpRoute(int port,String clientKey){
        JedisPool jedisPool  = RedisPoolDao.getRedisPool();
        Jedis jedis = jedisPool.getResource();
        boolean result = false;
        try{
            // RedisObjUtil.setUser();
            jedis.hdel(RedisKeyNameConfig.getUdpRout(clientKey),port+"");
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (jedis != null)
                jedis.close();
        }
        return result;
    }
}
