package com.zzz.pro.aop;

import com.zzz.pro.config.ApiLimit;
import com.zzz.pro.utils.RedisUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Component
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断请求是否属于方法的请求
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            //获取方法中的注解,看是否有该注解
            ApiLimit accessLimit = hm.getMethodAnnotation(ApiLimit.class);
            if (null == accessLimit) {
                return true;
            }

            //获取注解中的参数
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            /*
            //如果需要登录
            if(login){
                //获取登录的session进行判断
                //.....
                key+=""+"1";  //这里假设用户是1,项目中是动态获取的userId
            }
             */
            //自定义 key

            String ak = request.getLocalAddr() + key;
            //从redis中获取用户访问的次数
            Integer count = (Integer) redisUtil.get(ak);
            if (count == null) {
                //第一次访问
                redisUtil.set(ak, 1, 5);
            } else if (count < maxCount) {
                //加1
                redisUtil.incr(ak, 1);
            } else {
                //超出访问次数
                render(response, "请求失败");
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, String cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        out.write(cm.getBytes("utf-8"));
        out.flush();
        out.close();
    }
}