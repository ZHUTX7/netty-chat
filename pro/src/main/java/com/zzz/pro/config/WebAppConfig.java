//package com.zzz.pro.config;
//
//import com.zzz.pro.aop.AccessLimitInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//import javax.annotation.Resource;
//
//@Configuration
//public class WebAppConfig extends WebMvcConfigurerAdapter {
//    // 多个拦截器组成一个拦截器链
//    // addPathPatterns 用于添加拦截规则
//    // excludePathPatterns 用户排除拦截
//
//    @Resource
//    private AccessLimitInterceptor accessLimitInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 1. token拦截器
////        registry.addInterceptor(new InterceptorConfig2())//添加拦截器
////                .excludePathPatterns("/user/login")//对应的不拦截的请求
////                .excludePathPatterns("/user/register")
////                .addPathPatterns("/**"); //拦截所有请求
//        // 2. api保护器
//        registry.addInterceptor(accessLimitInterceptor)//添加拦截器
//                .addPathPatterns("/**"); //拦截所有请求
//    }
//}
//
