package com.proxy.server.service;

import com.proxy.server.util.HostInfoUtil;

/**
 * @author : ztx
 * @version :V1.0
 * @description :
 * @update : 2021/5/6 15:19
 */
public class ApmServiceImpl implements ApmService{
    @Override
    public int getCpuLoad() {
        return HostInfoUtil.getCpuLoad();
    }

    @Override
    public int getMemoryLoad() {
        return HostInfoUtil.getMemoryLoad();
    }

    @Override
    public int getDiskLoad() {
        return 0;
    }

    @Override
    public int getNetworkLoad() {
        return 0;
    }
}
