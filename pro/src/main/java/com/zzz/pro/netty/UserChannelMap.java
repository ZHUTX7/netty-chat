package com.zzz.pro.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * @author ztx
 * @date 2021-12-16 16:36
 * @description :
 */
public class UserChannelMap {
    //单例静态
    private static class SingletonMap {
        static final HashMap<String, Channel> instance = new HashMap<>();
    }

    public static HashMap<String, Channel> getInstance()
    {
        return UserChannelMap.SingletonMap.instance;
    }

    public static void output() {
        for (HashMap.Entry<String, Channel> entry : UserChannelMap.getInstance().entrySet()) {
            System.out.println("UserId: " + entry.getKey()
                    + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
