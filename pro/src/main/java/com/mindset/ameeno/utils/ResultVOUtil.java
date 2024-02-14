package com.mindset.ameeno.utils;

import com.mindset.ameeno.pojo.result.SysJSONResult;

public class ResultVOUtil {
    public static SysJSONResult success() {
        SysJSONResult resultVO = new SysJSONResult();
        resultVO.setData(null);
        resultVO.setStatus(200);
        resultVO.setMsg("成功");
        return resultVO;
    }
    public static SysJSONResult success(Object object) {
        SysJSONResult resultVO = new SysJSONResult();
        resultVO.setData(object);
        resultVO.setStatus(200);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static SysJSONResult success(String msg,Object object) {
        SysJSONResult resultVO = new SysJSONResult();
        resultVO.setData(object);
        resultVO.setStatus(200);
        resultVO.setMsg(msg);
        return success(null);
    }

    public static SysJSONResult error(Integer code, String msg) {
        SysJSONResult resultVO = new SysJSONResult();
        resultVO.setStatus(code);
        resultVO.setMsg(msg);
        return resultVO;
    }
}
