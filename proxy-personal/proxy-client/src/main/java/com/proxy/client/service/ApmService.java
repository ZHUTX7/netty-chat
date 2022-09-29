package com.proxy.client.service;

/**
 * @author : ztx
 * @version :V1.0
 * @description :  采集本机性能信息
 *                 1. CPU  2. MEMORY 3. Disk 4. network
 * @update : 2021/5/6 15:16
 */

public interface ApmService {
    //1.CPU总占比
    int getCpuLoad();

    //2.MEMORY
    int getMemoryLoad();

    //3.DIsk
    int getDiskLoad();

    //4. network
    int getNetworkLoad();
}
