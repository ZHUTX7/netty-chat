package com.proxy.server.dto;

import com.proxy.common.entity.server.ProxyRealServer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 代理客户端DTO (网关)，用于Redis读取
 * @update : 2021/2/3 15:07
 */

public class GatewayDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private int user_id;
    private String token;
    private String client_ip;
    private String user_name;
    private List<ProxyRealServer> route;//路由信息 Map<ip,port>

    public GatewayDTO(){}

    public GatewayDTO(int user_id, String token, String client_ip, String user_name, List<ProxyRealServer> route) {
        this.user_id = user_id;
        this.token = token;
        this.client_ip = client_ip;
        this.user_name = user_name;
        this.route = route;
    }

    public List<ProxyRealServer> getRoute() {
        return route;
    }

    public void setRoute(List<ProxyRealServer> route) {
        this.route = route;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
