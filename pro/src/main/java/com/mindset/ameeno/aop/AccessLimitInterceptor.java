package com.mindset.ameeno.aop;

import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.config.ApiLimit;
import com.mindset.ameeno.utils.JsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisStringUtil redisStringUtil;

    private Map<String,String> resultMap = new HashMap<>(){
        {
            put("status","401");
            put("msg","规则时间内请求数量超出上线,请勿频繁调用接口");
            put("data",null);
        }
    };
    private String result = JsonUtils.objectToJson(resultMap);

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
            String value = redisStringUtil.get(ak);
            //第一次访问
            if(StringUtils.isEmpty(value)){
                redisStringUtil.set(ak, 1+"", seconds);
                return true;
            }

            Integer count = Integer.parseInt(value) ;
            if (count < maxCount) {
                //加1
                redisStringUtil.incr(ak, 1);
                return true;
            } else {
                //超出访问次数
                render(response, result);
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