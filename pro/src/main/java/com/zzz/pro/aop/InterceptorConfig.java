//package com.zzz.pro.aop;
//
//import com.zzz.pro.utils.JWTUtils;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.Resource;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author : ztx
// * @version :V1.0
// * @description : 拦截器，前端与本后端通信需要token令牌
// * @update : 2020/9/15 21:42
// */
//
//@Configuration
//public class InterceptorConfig implements WebFilter {
//
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        ServerHttpRequest serverHttpRequest = exchange.getRequest();
//        String url=serverHttpRequest.getURI().getPath();
//
//        if(serverHttpRequest.getURI().getPath().equals("/user/login")||serverHttpRequest.getURI().getPath().startsWith("/icon"))
//            return chain.filter(exchange);
//        //此处添加逻辑代码验证token
//        try{
//            HttpHeaders headers = serverHttpRequest.getHeaders();
//            List<String> list = headers.get("token");
//            String token=list.get(0);
//            //1.验证， 并获取验证结果
//            Map<String,Object> map = JWTUtils.verify(token);
//
//            //2.检测token是否符合规范,不涉及是否过期
//            if((boolean)map.get("state") ){
////                String userId = JWTUtils.getClaim(token,"userId");
////                //3.测试token是否过期
////                if(!redisDao.flushToken(userId)){
////                    String msg = "{\"errorCode\":\"-1\",\"message\":\""+"登录超时，请重新登录 :)  "+"\"}";
////                    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
////                    org.springframework.core.io.buffer.DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
////                    //redisDao.delConfigByToken(token,userId);
////                    return exchange.getResponse().writeWith(Mono.just(buffer));
////                }
////                else {
////                    return chain.filter(exchange);
////                }
//                return exchange.getResponse().writeWith(map);
//
//            } else{
//                //反馈不符合规范的具体信息
//                if((int)map.get("token_code") == -3 ){
//                    String user_id = JWTUtils.getUserIDByToken(token,"user_id");
//                    redisDao.delConfigByToken(token,user_id);
//                }
//
//                String msg = "{\"errorCode\":\"-1\",\"message\":\""+(String)map.get("msg")+"\"}";
//                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
//                org.springframework.core.io.buffer.DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//                return exchange.getResponse().writeWith(Mono.just(buffer));
//            }
//        }catch (Exception e){   //当没有从cookies获取到token
//            e.printStackTrace();
//            String msg = "{\"errorCode\":\"-1\",\"message\":\""+"登录令牌失效或不存在，重新登录"+"\"}";
//            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
//            org.springframework.core.io.buffer.DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//            return exchange.getResponse().writeWith(Mono.just(buffer));
//        }
//
//    }
//}
//
