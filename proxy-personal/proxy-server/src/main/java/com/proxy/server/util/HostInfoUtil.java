package com.proxy.server.util;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 采集本机CPU信息..
 * @update : 2021/5/6 14:54
 */
import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;


public class HostInfoUtil {
    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    //获取内存占用百分比
    public static int getMemoryLoad() {
        double totalVirtualMemory = osmxb.getTotalPhysicalMemorySize();

        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();

        double value = freePhysicalMemorySize/totalVirtualMemory;

        int percentMemoryLoad = (int) ((1-value)*100);

        return percentMemoryLoad;

    }

    //获取CPU占用率百分比
    public static int getCpuLoad() {
        double cpuLoad = osmxb.getSystemCpuLoad();
        int percentCpuLoad = (int) (cpuLoad * 100);
        return percentCpuLoad;

    }


    public static void main(String[] args) throws InterruptedException {
        while (true){
            System.out.println("CPU占用率："+getCpuLoad());
            System.out.println("内存占用率："+getMemoryLoad());
            Thread.sleep(2000);
        }

    }

}
