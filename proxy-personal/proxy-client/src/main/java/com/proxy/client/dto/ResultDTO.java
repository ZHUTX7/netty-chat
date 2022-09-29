package com.proxy.client.dto;

import java.util.Map;

/**
 * @author ztx
 * @date 2021-09-13 16:21
 * @description : 返回dto对象
 */
public class ResultDTO {
    private int errorCode ;
    private String message;
    private Map<String,Object> playlod;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getPlaylod() {
        return playlod;
    }

    public void setPlaylod(Map<String, Object> playlod) {
        this.playlod = playlod;
    }
}
