package com.zzz.pro.exception;

import com.zzz.pro.pojo.result.SysJSONResult;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 8140508447825501039L;
    private SysJSONResult sysJSONResult;

    public ApiException(SysJSONResult sysJSONResult) {
        this.sysJSONResult = sysJSONResult;
        log.error("调用API错误 : "+sysJSONResult.getMsg());
    }

    public ApiException() {
        this.sysJSONResult = new SysJSONResult();
        sysJSONResult.setMsg("调用错误");
        sysJSONResult.setStatus(500);
    }

    public ApiException(Integer code,String msg){
        this.sysJSONResult = new SysJSONResult();
        sysJSONResult.setStatus(code);
        sysJSONResult.setMsg(msg);
    }

    public SysJSONResult getSysJSONResult() {
        return sysJSONResult;
    }
}