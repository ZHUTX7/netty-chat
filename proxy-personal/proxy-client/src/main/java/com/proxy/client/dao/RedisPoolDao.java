package com.proxy.client.dao;

import com.proxy.client.service.ClientBeanManager;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 单例模式 ,获取Redis配置
 * @update : 2021/2/1 18:04
 */
public  class RedisPoolDao {
    private  static final String IP ;
    private  static final int PORT ;
    private  static final String PASSWORD ;
    private static JedisPoolConfig config = null;
    private static JedisPool jedisPool = null;

    static {
        PORT = Integer.parseInt(ClientBeanManager.getConfigService().readConfig().get("redisPort"));
        IP = ClientBeanManager.getConfigService().readConfig().get("redisIP");
        PASSWORD = ClientBeanManager.getConfigService().readConfig().get("redisPassword");
    }

    private static JedisPoolConfig getConfig(){
        if(config == null){
            //设置连接池的配置对象
            config = new JedisPoolConfig();
            //设置连接池参数
            config.setMaxTotal(30);
            config.setMaxIdle(10);
        }
        return config;
    }
    public static JedisPool getRedisPool(){
        if(jedisPool == null){

            //获取连接池对象
            jedisPool = new JedisPool(RedisPoolDao.getConfig(), IP, PORT,10000,PASSWORD);
        }
        return jedisPool;
    }


}
