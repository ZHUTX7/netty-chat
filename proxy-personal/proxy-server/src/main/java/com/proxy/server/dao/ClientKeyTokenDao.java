package com.proxy.server.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 本地保存已经登录的对象。主要用于将 token - clientID - config进行关联
 * @update : 2021/4/6 15:52
 */
public class      ClientKeyTokenDao {
    private static Map<String ,String> map = new HashMap<>();

    public static Map<String, String> getMap(){
        if(map == null){
            map = new  HashMap<>();
            return map;
        }
        return map;
    };

    // 用户登录上，存储链接关系 key:clientKey , value:token
    public static void addUserLogin(String clientKey,String token){
        if(map == null){
            map = new HashMap<>();
        }
        map.put(clientKey,token);
    }

    //通过clientKey去取 token
    public static String getTokenByClientKey(String clientKey){
        if(map == null){
            return null;
        }
       return map.get(clientKey);
    }
}
