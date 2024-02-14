package com.mindset.ameeno.pojo.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ztx
 * @date 2021-12-03 11:17
 * @description :
 */
@Data
public class SysJSONResult<T> implements Serializable {
    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private T data;

}
