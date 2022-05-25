//package com.zzz.pro.aop;
//
//import com.zzz.pro.utils.JWTUtils;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
// TODO： 没有登录状态下也可以查看广场动态
//public class InterceptorConfig2 implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("token");
//        return (boolean)JWTUtils.verify(token).get("state");
//
//    }
//}
