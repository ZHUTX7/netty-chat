package com.zzz.pro.pojo.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebSocketMsg<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer SocketCode;
    private T msg;
}
