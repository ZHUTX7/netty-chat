package com.proxy.server.service;

import com.proxy.common.dto.ResultDTO;
import com.proxy.common.entity.server.ProxyRealServer;

/**
 * @author ztx
 * @date 2021-09-13 15:29
 * @description : 网关代理管理，主要用于 启停某些开关的代理
 */
public interface ProxyManager {

    //1. 开启某个端口的代理，同时监听端口
    // 开启端口，同时需要把路由信息写入内存
    ResultDTO openPort(String clientKey, int port, ProxyRealServer proxyRealServer);

    //2. 关闭某个端口的代理
    ResultDTO closeProxy(int port);


    //3. 分配端口代理端口的代理
    ResultDTO openDynamicProxy( ProxyRealServer proxyRealServer);

}
