package com.mindset.ameeno.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
@Slf4j
@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public SysJSONResult handlerException(ApiException e) {
        if(e.isLog()){
            e.printStackTrace();
            log.error("error info:", e);
        }
        return ResultVOUtil.error(e.getSysJSONResult().getStatus(), e.getSysJSONResult().getMsg());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = TokenExpiredException.class)
    @ResponseBody
    public SysJSONResult handlerTokenException(TokenExpiredException e) {
        log.info("token过期");
        return ResultVOUtil.error(4011,"token过期");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public SysJSONResult Exception(Exception e) {
        e.printStackTrace();
        log.error("error info:", e);
        return ResultVOUtil.error(-1, e.getMessage());
    }
}
