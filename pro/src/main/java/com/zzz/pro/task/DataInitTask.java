//package com.zzz.pro.task;
//
//
//import com.zzz.pro.config.RedissionBloom;
//import com.zzz.pro.dao.UserRepository;
//import org.redisson.api.RBloomFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//import javax.annotation.Resource;
//
//
////数据预热任务
//@Service
//public class DataInitTask {
//    // 预期插入数量
//    static long expectedInsertions = 200L;
//    // 误判率
//    static double falseProbability = 0.01;
//
//
//    @Resource
//    private RedissionBloom redissionBloom;
//
//    @Resource
//    private UserRepository userRepository;
//
//    @Bean
//    public RBloomFilter<Integer> init() {
//        // 启动项目时初始化bloomFilter
//        System.out.println("数据预热中>>>>>>>>>>>>>>>>>>>>");
//
//        RBloomFilter<Integer>  bloomFilter = redissionBloom.create("usertList", expectedInsertions, falseProbability);
//        bloomFilter.add(p.getId());
//
//        System.out.println("数据预热完成------------------");
//        return bloomFilter;
//    }
//
//
//}
//
