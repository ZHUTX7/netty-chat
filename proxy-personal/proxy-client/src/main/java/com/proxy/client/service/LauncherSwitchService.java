package com.proxy.client.service;

import com.proxy.client.ProxyClient;

import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @description :   为了后续兼容更多的系统，此处添加启动器
 * @update : 2021/4/16 15:24
 */

public class LauncherSwitchService {
    public static  void start(String... list) throws InterruptedException {

        //读取启动类型
        String launchedType = ClientBeanManager.getConfigService().readConfig().get("launchedType");
        String gatewayHost = null;
        String gatewayPort = null;

        switch (launchedType){

            //2.Linux启动
            case "linux" :
                gatewayHost = ClientBeanManager.getConfigService().readConfig().get("server.host");
                gatewayPort = ClientBeanManager.getConfigService().readConfig().get("server.port");
                new ProxyClient(gatewayHost, Integer.parseInt(gatewayPort)).start(); break;

            //4.IDEA开发环境启动
            case "idea" :
                gatewayHost = ClientBeanManager.getConfigService().readConfig().get("server.host");
                gatewayPort = ClientBeanManager.getConfigService().readConfig().get("server.port");
                new ProxyClient(gatewayHost, Integer.parseInt(gatewayPort)).start(); break;

        }

    };


}
