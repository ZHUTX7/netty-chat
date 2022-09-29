package com.proxy.server.service;

import com.proxy.common.dto.ResultDTO;

/**
 * @author ztx
 * @date 2022-01-07 14:38
 * @description :路由同步接口
 */
public interface RouteSync {
    //全局client同步
    ResultDTO clientRouteSync();
    //单个client同步
    ResultDTO clientRouteSync(String clientKey);

}
