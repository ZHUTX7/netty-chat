package com.proxy.common.protocol;


/**
 * @author : ztx
 * @version :V1.0
 * @description :
 * @update : 2021/4/20 11:49
 */

public class RedisKeyNameConfig {
    private static final String CLIENT_REGISTER_LIST = "proxy-client:login:online:key";
    private static final String ROUTE_CONFIG = "proxy-client:routeConfig";
    private static final String TCP_ROUTE_CONFIG = "proxy-client:tcpRout";
    private static final String UDP_ROUTE_CONFIG = "proxy-client:udpRout";
    private static final String HTTP_ROUTE_CONFIG = "proxy-client:httpRout";
    private static final String USER_CONNECT_CONFIG="rank:userConnectTime";
    private static final String HOST_CONNECT_CONFIG="rank:hostConnectTime";
    private static final String TCP_PROXY_LIST = "tcp:proxy:list";
    private static final String UDP_PROXY_LIST = "udp:proxy:list";

    public static String getTcpProxyList() {
        return TCP_PROXY_LIST;
    }

    public static String getUdpProxyList() {
        return UDP_PROXY_LIST;
    }

    public static String getTcpRout(String clientKey) {
        return TCP_ROUTE_CONFIG+clientKey;
    }
    public static String getHttpRout() {
        return HTTP_ROUTE_CONFIG;
    }
    public static String getUdpRout(String clientKey) {
        return UDP_ROUTE_CONFIG+clientKey;
    }


    public static String getClientRegisterList() {
        return CLIENT_REGISTER_LIST;
    }

    public static String getRouteConfig() {
        return ROUTE_CONFIG;
    }

    public static String getHostConnectConfig() {
        return HOST_CONNECT_CONFIG;
    }

    public static String getUserConnectConfig() {
        return USER_CONNECT_CONFIG;
    }
}
