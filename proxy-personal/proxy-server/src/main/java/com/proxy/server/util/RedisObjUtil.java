package com.proxy.server.util;

import com.alibaba.fastjson.JSON;
import com.proxy.server.dto.GatewayRouteDTO;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author : ztx
 * @version :V1.0
 * @description : Redis存取对象（序列化）
 * @update : 2021/2/3 16:20
 */
public class RedisObjUtil {
    public static void setUser(Jedis jedis, String hash_name, String key,Object obj) throws Exception {
        ObjectOutputStream oos = null;  //对象输出流
        ByteArrayOutputStream bos = null;  //内存缓冲流
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        byte[] byt = bos.toByteArray();
        //jedis.set(key.getBytes(), byt);
        jedis.hset(hash_name.getBytes(),key.getBytes(),byt);
        bos.close();
        oos.close();
    }

    // 取对象
    public static Object getUser(Jedis jedis,String hash_name, String key) throws Exception {
        byte[] byt = jedis.hget(hash_name.getBytes(),key.getBytes());
        ObjectInputStream ois = null;  //对象输入流
        ByteArrayInputStream bis = null;   //内存缓冲流
        Object obj = null;
        bis = new ByteArrayInputStream(byt);
        ois = new ObjectInputStream(bis);
        obj = ois.readObject();
        bis.close();
        ois.close();
        return obj;
    }

    public static void setUserObj(Jedis jedis, String hash_name, String key, GatewayRouteDTO gatewayRouteDto) throws Exception {

        jedis.hset(hash_name,key, JSON.toJSONString(gatewayRouteDto));
    }

    // 取对象
    public static GatewayRouteDTO getProxyClient(Jedis jedis, String hash_name, String key) throws Exception {
        GatewayRouteDTO gatewayRouteDto = JSON.parseObject( jedis.hget(hash_name,key), GatewayRouteDTO.class);
        return gatewayRouteDto;
    }
}
