package com.proxy.server.dto;

import java.sql.Timestamp;

/**
 * @author ztx
 * @date 2021-11-29 17:40
 * @description : 发送给网关的日志
 */
public class GatewayLogDTO {
    //private
    private int id;
    private String gateway_ip;
    private int type;//操作类型 ：1. 路由2.端口3.HTTP
    private String content;//操作内容
    private int result;//结果
    private Timestamp time;
    private int rank_level;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGateway_ip() {
        return gateway_ip;
    }

    public void setGateway_ip(String gateway_ip) {
        this.gateway_ip = gateway_ip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }



    public int getRank_level() {
        return rank_level;
    }

    public void setRank_level(int rank_level) {
        this.rank_level = rank_level;
    }
}
