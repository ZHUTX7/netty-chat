//package com.zzz.pro.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class ApiUseLog {
//
//    @Around("execution(* com.zzz.pro.controller.*.*(..))")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        System.out.println("开始执行切面时间"+start);
//
//        System.out.println("开始执行切面方法"+joinPoint.getSignature().getName());
//        Object proceed = joinPoint.proceed();
//
//        long end = System.currentTimeMillis();
//        System.out.println("结束执行切面时间"+end);
//        return proceed;
//    }
//}
