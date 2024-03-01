package com.mindset.ameeno.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zhutianxiang
 
 * @Date 2023/11/30 17:14
 * @Version 1.0
 */
@Service
public class WatchService {
    private static AtomicInteger currentUserCount = new AtomicInteger(0);
    private static AtomicInteger appConnSumCount = new AtomicInteger(0);
    private Set<String> todayOnlineUserSum = new HashSet<>();
    private Set<String> currentOnlineUserSum = new HashSet<>();
    @Async
    public void userUp(String userId){
        currentOnlineUserSum.add(userId);
        todayOnlineUserSum.add(userId);
    }
    @Async
    public void userOffline(String userId){
        currentOnlineUserSum.remove(userId);
    }
    public void getTodayUserUpCount(){

    }
}
