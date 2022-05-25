package com.zzz.pro.utils;


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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : ztx
 * @version :V1.0
 * @descr。iption : JWT工具类
 * @update : 2020/9/15 16:43
 */

public class JWTUtils {
    private String utils;
    private static final int TIME_UNIT = Calendar.HOUR;
    private static final int LIVE_TIME = 9999;
    private static final String KEY = "ztx123456";

    /**
     * @author : ztx
     * @version :V1.0
     * @description : 生成token （传入HashMap ， Map中保存用户基本信息（用户名，ID）
     * @update : 2020/9/15 16:43
     */
    public static String creatToken(Map<String,String> map){
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        //2021/8/10 以下token过期时间注释，过期完全由内存控制
//        Calendar instance = Calendar.getInstance();
//        instance.add(TIME_UNIT,LIVE_TIME);
//        builder.withExpiresAt(instance.getTime());

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
            map.put("token_code",-1);
            map.put("state",false);
            map.put("msg","token算法不一致!");
            return map;
        }catch (SignatureVerificationException e){
            e.printStackTrace();
            map.put("token_code",-2);
            map.put("state",false);
            map.put("msg","无效签名");
            return map;
        }catch (TokenExpiredException e){
            e.printStackTrace();
            map.put("token_code",-3);
            map.put("state",false);
            map.put("msg","token已经过期");
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("token_code",-4);
            map.put("state",false);
            map.put("msg","token无效");
            return map;
        }
        map.put("token_code",1);
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

}
