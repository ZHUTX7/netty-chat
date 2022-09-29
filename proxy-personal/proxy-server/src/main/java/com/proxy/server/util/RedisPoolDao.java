package com.proxy.server.util;

import com.proxy.server.service.ConfigService;
import com.proxy.server.service.ServerBeanManager;
import redis.clients.jedis.Jedis;
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
        ServerBeanManager.getConfigService().readServerConfig();
        ConfigService configService = ServerBeanManager.getConfigService();
        PORT = (int)configService.getConfigure("redisPort");
        IP = (String)configService.getConfigure("redisIP");
        PASSWORD = (String)configService.getConfigure("redisPassword");
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
