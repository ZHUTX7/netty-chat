package com.zzz.pro.aop;

/**
 * @Author zhutianxiang
 * @Description 自定义拦截器，实现注解鉴权和token验证
 * @Date 2023/8/6 16:32
 * @Version 1.0
 */
import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.enums.TokenStatusEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.utils.JWTUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    /** 设置响应头部信息 */
    private void setHeader(HttpServletRequest request,HttpServletResponse response){
        response.setHeader( "Set-Cookie" , "cookiename=httponlyTest;Path=/;Domain=domainvalue;Max-Age=seconds;HTTPOnly");
        response.setHeader( "Content-Security-Policy" , "default-src 'self'; script-src 'self'; frame-ancestors 'self'");
        response.setHeader("Access-Control-Allow-Origin", (request).getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Referrer-Policy","no-referrer");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    /** token有效期刷新和定期刷新 */
    private void refreshToken(HttpServletResponse response,String refreshToken){
        //判断token是否过期
        response.setHeader("token",   JWTUtils.flushToken(refreshToken,30, Calendar.MINUTE));
        response.setHeader("refreshToken", JWTUtils.flushToken(refreshToken,60*24*7,Calendar.MINUTE));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String token =  request.getHeader("token");
        String refreshToken = request.getHeader("refreshToken");

        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(refreshToken)){
            throw new ApiException(ResultEnum.TOKEN_ERROR.getCode(),ResultEnum.TOKEN_ERROR.getTitle());
        }
        // 获取当前token（获取的是请求头的token），同时会校验token是否过期，过期了会直接抛出异常，所以这里不用做额外处理。
        Map<String,Object> map = JWTUtils.verify(token);

        if (TokenStatusEnum.ACCESS_TOKEN.getCode().equals(map.get("token_code"))){
            return  true;
        }
        if(TokenStatusEnum.TIME_DELAY.getCode().equals(map.get("token_code"))){
            //token过期
            //2.验证refreshToken
            Map<String,Object> refreshMap = JWTUtils.verify(refreshToken);
            if(refreshMap.get("token_code").equals(TokenStatusEnum.ACCESS_TOKEN.getCode())) {
                //refreshToken验证成功 ,且没有过期
                //3.生成新的token
                refreshToken(response,refreshToken);
                setHeader(request,response);
                return true;
//                throw new ApiException(ResultEnum.TOKEN_FLUSH.getCode(),ResultEnum.TOKEN_FLUSH.getTitle());
            }
        }

        throw new ApiException(ResultEnum.TOKEN_ERROR.getCode(),ResultEnum.TOKEN_ERROR.getTitle());
    }
}

