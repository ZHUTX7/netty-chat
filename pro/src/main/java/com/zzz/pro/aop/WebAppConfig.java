//package com.zzz.pro.aop;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//public class WebAppConfig extends WebMvcConfigurerAdapter {
//    // 多个拦截器组成一个拦截器链
//    // addPathPatterns 用于添加拦截规则
//    // excludePathPatterns 用户排除拦截
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new InterceptorConfig2())//添加拦截器
//                .excludePathPatterns("/user/login")//对应的不拦截的请求
//                .excludePathPatterns("/user/register")
//                .addPathPatterns("/**"); //拦截所有请求
//    }
//}
//
