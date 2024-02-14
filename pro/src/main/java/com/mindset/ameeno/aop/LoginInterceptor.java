package com.mindset.ameeno.aop;

/**
 * @Author zhutianxiang
 * @Description 自定义拦截器，实现注解鉴权和token验证
 * @Date 2023/8/6 16:32
 * @Version 1.0
 */
import com.mindset.ameeno.enums.TokenStatusEnum;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
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
        //获取IP地址
        request.getRemoteAddr();
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
            }else {
                //refreshToken过期
                //过期抛异常
                log.info("Token过期------");
                throw new ApiException(ResultEnum.TOKEN_ERROR.getCode(),ResultEnum.TOKEN_ERROR.getTitle());
            }
        }

        throw new ApiException(ResultEnum.TOKEN_ERROR.getCode(),ResultEnum.TOKEN_ERROR.getTitle());
    }
}

