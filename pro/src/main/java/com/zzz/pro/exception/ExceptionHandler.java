package com.zzz.pro.exception;

import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
@Slf4j
@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public SysJSONResult handlerMicrosegException(ApiException e) {
        e.printStackTrace();
        log.error("error info:", e);
        return ResultVOUtil.error(e.getSysJSONResult().getStatus(), e.getSysJSONResult().getMsg());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public SysJSONResult Exception(Exception e) {
        e.printStackTrace();
        log.error("error info:", e);
        return ResultVOUtil.error(-1, e.getMessage());
    }
}
