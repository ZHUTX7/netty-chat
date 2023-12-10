package com.zzz.pro.service;

import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
/**
 * @Author zhutianxiang
 * @Description 布隆过滤器 - 用户匹配黑名单
 * @Date 2023/10/21 22:21
 * @Version 1.0
 */
@Service
@Configuration
@Slf4j
public class BloomFilterService {

    private static final int SIZE = 1000000; // 布隆过滤器的大小
    private static final int[] PRIMES = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47 }; // 哈希函数的种子


    @Resource
    private RedisStringUtil redisStringUtil;


    public void add(String userId, String value) {
        String  key = RedisKeyEnum.USER_BLACK_POOL.getCode()+userId;
        int[] hashes = getHashes(value);
        for (int hash : hashes) {
            redisStringUtil.getRedisTemplate().opsForValue().setBit(key, hash, true);
        }
    }

    public boolean mightContain(String userId, String value) {
        String  key = RedisKeyEnum.USER_BLACK_POOL.getCode()+userId;
        int[] hashes = getHashes(value);
        for (int hash : hashes) {
            if (!redisStringUtil.getRedisTemplate().opsForValue().getBit(key, hash)) {
                //TODO log
                log.info("用户{}黑名单中存在用户{}",userId,value);
                return false;
            }
        }
        return true;
    }

    private int[] getHashes(String value) {
        int[] hashes = new int[PRIMES.length];
        for (int i = 0; i < PRIMES.length; i++) {
            hashes[i] = hash(value, PRIMES[i]) % SIZE;
        }
        return hashes;
    }

    private int hash(String value, int prime) {
        int hash = 0;
        for (char c : value.toCharArray()) {
            hash = (hash * prime + c) & 0x7fffffff; // 使用位运算符确保结果是非负整数
        }
        return Math.abs(hash); // 取绝对值确保结果是非负整数
    }

    public void remove(String userId){
        String  key = RedisKeyEnum.USER_BLACK_POOL.getCode()+userId;
        redisStringUtil.getRedisTemplate().delete(key);
    }


}
