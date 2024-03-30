package com.mindset.ameeno.utils;


import com.mindset.ameeno.pojo.bo.ApplePurchaseBO;
import io.jsonwebtoken.*;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

/**
 * @description: IOS 生成对应的token
 * @author lijian
 * @date 2021/11/29 14:47
 * @version 1.0
 */
@Slf4j
@Service
public class IosUtils {

    public static String getToken() {

        Map<String, Object> header = new HashMap<>();

        header.put("alg", "ES256");
        header.put("kid", "8NHM4JC2V8");
        header.put("typ", "JWT");

        Map<String, Object> claim = new HashMap<>();
        claim.put("iss", "cfe53cba-19d6-4c93-a562-fc36c8c108fc");
        claim.put("iat", Math.floor(System.currentTimeMillis() / 1000));
        //claim.put("exp", DateUtil.addTime(currentDate,DateUtil.MINUTE,60).getTime());
        claim.put("exp", Math.floor(System.currentTimeMillis() / 1000) + 1800);
        claim.put("aud", "appstoreconnect-v1");
        claim.put("nonce", UUID.randomUUID());
        claim.put("bid", "com.mindset.ameeno");
        PrivateKey privateKey = getECPrivateKey();
        try {
            JwtBuilder jwtBuilder = Jwts.builder().setHeader(header).setClaims(claim)
                    .signWith(SignatureAlgorithm.ES256, privateKey);

            String token = jwtBuilder.compact();
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "";
    }

//    public static void main(String[] args) {
//        System.out.println(getToken());;
//    }

    /**
     * 获取PrivateKey对象
     *
     * @return
     */
    private static PrivateKey getECPrivateKey() {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    Base64.decodeBase64("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgU+rMIFjJqsuCbqmE" +
                            "fzPrGtuUVCbw43xYPmziqJ/HMNGgCgYIKoZIzj0DAQehRANCAAT6DfPeu5zp/Nnc" +
                            "OQ8ZtJADseVpUgFv91IaY/CJue/nkPe2sUUWH/ZHRPy2y2uYLI/hUhPK7cPx4S/S" +
                            "vg6eejt9"));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String verifyTransaction(String base64Transaction){
        try{
            String[] str =   base64Transaction.split("\\.");
            String json =  str[1];
            ApplePurchaseBO purchaseBO = JsonUtils.jsonToPojo(json, ApplePurchaseBO.class);
            if(purchaseBO.getInAppOwnershipType().equals("PURCHASED")){
                return JsonUtils.objectToJson(purchaseBO);
            }else
                return "";
        }catch (Exception e){
            log.error("transaction verify failed ! : {}",e);
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(getToken());
    }
}