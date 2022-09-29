package com.proxy.server.dto;

import com.proxy.common.entity.server.ProxyRealServer;

import java.io.Serializable;
import java.util.List;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 用户DTO ，用于Redis读取
 * @update : 2021/2/3 15:07
 */

public class GatewayRouteDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private int gateway_id;
    private String proxy_client_key;
    private String gateway_ip;
    private List<ProxyRealServer> route;//路由信息 Map<ip,port>

    public GatewayRouteDTO(){}

    public GatewayRouteDTO(int gateway_id, String token, String gateway_ip, String proxy_client_key, List<ProxyRealServer> route) {
        this.gateway_id = gateway_id;
        this.gateway_ip = gateway_ip;
        this.proxy_client_key = proxy_client_key;
        this.route = route;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(int gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getProxy_client_key() {
        return proxy_client_key;
    }

    public void setProxy_client_key(String proxy_client_key) {
        this.proxy_client_key = proxy_client_key;
    }


    public String getGateway_ip() {
        return gateway_ip;
    }

    public void setGateway_ip(String gateway_ip) {
        this.gateway_ip = gateway_ip;
    }

    public List<ProxyRealServer> getRoute() {
        return route;
    }

    public void setRoute(List<ProxyRealServer> route) {
        this.route = route;
    }
}
