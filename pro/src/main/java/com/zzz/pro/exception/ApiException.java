package com.zzz.pro.exception;

import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.pojo.result.SysJSONResult;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 8140508447825501039L;
    private SysJSONResult sysJSONResult;
    private boolean isLog;

    public ApiException(SysJSONResult sysJSONResult) {
        this.sysJSONResult = sysJSONResult;
        this.isLog = true;
        log.error("调用API错误 : "+sysJSONResult.getMsg());
    }

    public ApiException() {
        this.sysJSONResult = new SysJSONResult();
        this.isLog = true;
        sysJSONResult.setMsg("调用错误");
        sysJSONResult.setStatus(ResultEnum.SUCCESS.getCode());
    }

    public ApiException(Integer code,String msg){
        this.sysJSONResult = new SysJSONResult();
        // token无效或客户端传值错误不打印日志
        if(code== ResultEnum.PARAM_ERROR.getCode() || code==ResultEnum.TOKEN_ERROR.getCode()){
            this.isLog = false;
        }else{
            this.isLog = true;
        }
        sysJSONResult.setStatus(code);
        sysJSONResult.setMsg(msg);
    }

    public SysJSONResult getSysJSONResult() {
        return sysJSONResult;
    }

    public boolean isLog() {
        return isLog;
    }
}