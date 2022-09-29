package com.proxy.server.service;

import java.util.Map;

/**
 * @author ztx
 * @date 2021-11-29 18:36
 * @description :
 */
public interface SendMessage {
    void sendMessage(Map<String,Object> message) throws Exception;
}
