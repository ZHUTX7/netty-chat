package com.zzz.pro.pojo.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author ztx
 * @date 2021-12-03 11:17
 * @description :
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysJSONResult {
    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;



    public SysJSONResult() {
    }


    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓构造消息体↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    public SysJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public SysJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public static SysJSONResult build(Integer status, String msg, Object data) {
        return new SysJSONResult(status, msg, data);
    }

    public static SysJSONResult ok(Object data,String msg) {
        return new SysJSONResult(200,msg,data);
    }
    public static SysJSONResult ok(Object data) {
        return new SysJSONResult(data);
    }

    public static SysJSONResult ok() {
        return new SysJSONResult(null);
    }

    public static SysJSONResult errorMsg(String msg) {
        return new SysJSONResult(500, msg, null);
    }

    public static SysJSONResult errorMap(Object data) {
        return new SysJSONResult(501, "error", data);
    }

    public static SysJSONResult errorTokenMsg(String msg) {
        return new SysJSONResult(502, msg, null);
    }

    public static SysJSONResult errorException(String msg) {
        return new SysJSONResult(555, msg, null);
    }

    //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑构造消息体↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    // GET SET方法
    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }



}
