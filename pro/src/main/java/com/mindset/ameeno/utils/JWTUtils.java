package com.mindset.ameeno.utils;


/**
 * @author : admin
 * @version :V1.0
 * @description :
 * @update : 2021/1/11 11:12
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mindset.ameeno.enums.TokenStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @descr。iption : JWT工具类
 * @update : 2020/9/15 16:43
 */
@Slf4j
public class JWTUtils {

    private static final String KEY = "ztx123456";

    /**
     * @author : ztx
     * @version :V1.0
     * @description : 生成token （传入HashMap ， Map中保存用户基本信息（用户名，ID）
     * @update : 2020/9/15 16:43
     */
    public static String creatToken(Map<String,String> map,int liveTime,int timeUnit){
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        //设置过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(timeUnit,liveTime);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(KEY)).toString();
    }


    //2. 验证TOKEN
    public static HashMap<String,Object> verify(String token){
        HashMap<String,Object> map = new HashMap<>();
        try{
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
            decodedJWT.getClaims().forEach((k,v) -> {
                map.put(k,v);
            });
        }catch (AlgorithmMismatchException e){
            e.printStackTrace();
            map.put("token_code", TokenStatusEnum.METHOD_WRONG.getCode());
            map.put("state",false);
            map.put("msg",TokenStatusEnum.METHOD_WRONG.getTitle());
            return map;
        }catch (SignatureVerificationException e){
            e.printStackTrace();
            map.put("token_code",TokenStatusEnum.SIGN_WRONG.getCode());
            map.put("state",false);
            map.put("msg",TokenStatusEnum.SIGN_WRONG.getTitle());
            return map;
        }catch (TokenExpiredException e){
            log.info("token过期");
            map.put("token_code",TokenStatusEnum.TIME_DELAY.getCode());
            map.put("state",false);
            map.put("msg",TokenStatusEnum.TIME_DELAY.getTitle());
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("token_code",TokenStatusEnum.TOKEN_WRONG.getCode());
            map.put("state",false);
            map.put("msg",TokenStatusEnum.TOKEN_WRONG.getTitle());
            return map;
        }
        map.put("token_code",TokenStatusEnum.ACCESS_TOKEN.getCode());
        map.put("msg",TokenStatusEnum.ACCESS_TOKEN.getTitle());
        map.put("state",true);
        return map;

    }

    //
    public static DecodedJWT getToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
    }

    //根据token获取信息
    //传参：1.token 2.想要获取数据的变量名 ， 如:username , password , userId ,nickname
    public static String getClaim(String token,String target_info){
        Map<String, Claim> info = JWTUtils.getToken(token).getClaims();
        return info.get(target_info).asString();
    }

    public static String flushToken(String token,int liveTime,int timeUnit){
        Map<String, Claim> info = JWTUtils.getToken(token).getClaims();
        Map<String,String> map = new HashMap<>();
        info.forEach((k,v)->{
            map.put(k,v.asString());
        });
        return creatToken(map,liveTime,timeUnit);
    }


//    public static void main(String[] args) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("username","ztx");
//        map.put("password","123456");
//        String token = creatToken(map, 1*60*24*7,Calendar.MINUTE);
//        String token2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyUm9sZSI6IjEiLCJleHAiOjE2OTIxODA4MjMsInVzZXJJZCI6ImEwOTM0OWFkIiwiZGV2aWNlSWQiOiJJT1MtNDg5NjZiODExMzdjOWMwNzdlZGEyMTgyMTZiMTE3ODJlMTI0ZTE4ZGJjNjIyY2UxNDkzYmQyYzJlZWIzMDBkNyJ9.lfoM9CTpQAa4jV1NqEjRzPki3VhlrTCliJeGFfQbUfU";
//
//        Date date = JWT.decode(token2).getClaims().get("exp").asDate();
//        System.out.println(date);
//
//    }
}
