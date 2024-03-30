package com.mindset.ameeno.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mindset.ameeno.controller.form.RefundIosForm;
import com.mindset.ameeno.pojo.bo.AppleReceiptBO;
import com.mindset.ameeno.service.SKUService;
import com.mindset.ameeno.utils.HttpClientUtils;
import com.mindset.ameeno.utils.IosUtils;
import com.mindset.ameeno.utils.JsonUtils;
import com.mindset.ameeno.utils.JwsUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhutianxiang
 * @Description  apple api
 *   /*通知类型
 *  原文链接： https://developer.apple.com/documentation/appstoreservernotifications/notificationtype
 *              CONSUMPTION_REQUEST 表示客户针对消耗品内购发起退款申请
 *              DID_CHANGE_RENEWAL_PREF 对其订阅计划进行了更改 如果subtype是UPGRADE，则用户升级了他们的订阅;如果subtype是DOWNGRADE，则用户将其订阅降级或交叉分级
 *              DID_CHANGE_RENEWAL_STATUS 通知类型及其subtype指示用户对订阅续订状态进行了更改
 *              DID_FAIL_TO_RENEW 一种通知类型及其subtype指示订阅由于计费问题而未能续订
 *              DID_RENEW 一种通知类型，连同其subtype指示订阅成功续订
 *              EXPIRED 一种通知类型及其subtype指示订阅已过期
 *              GRACE_PERIOD_EXPIRED 表示计费宽限期已结束，无需续订，因此您可以关闭对服务或内容的访问
 *              OFFER_REDEEMED 一种通知类型，连同其subtype指示用户兑换了促销优惠或优惠代码。 subtype DID_RENEW
 *              PRICE_INCREASE 一种通知类型，连同其subtype指示系统已通知用户订阅价格上涨
 *              REFUND 表示 App Store 成功为消耗性应用内购买、非消耗性应用内购买、自动续订订阅或非续订订阅的交易退款
 *              REFUND_DECLINED 表示 App Store 拒绝了应用开发者发起的退款请求
 *              RENEWAL_EXTENDED 表示 App Store 延长了开发者要求的订阅续订日期
 *              REVOKE表示 用户有权通过家庭共享获得的应用内购买不再通过共享获得
 *              SUBSCRIBED 一种通知类型，连同其subtype指示用户订阅了产品
 *              1. 用户主动取消订阅notificationType:DID_CHANGE_RENEWAL_STATUS
 *              2. 用户取消订阅，又重新开通连续订阅notificationType: SUBSCRIBED  subtype: RESUBSCRIBE
 *              3. 用户首次开通订阅notificationType: SUBSCRIBED  subtype: INITIAL_BUY
 *
 */
@Slf4j
@Service
public class AppleService {
    @Resource
    private HttpClientUtils httpClientUtils;
    @Resource
    private IosUtils iosUtils;

    @Value("${applepay.verifyUrl}")
    private String url_apple_pay;
    @Value("${applepay.verifyTestUrl}")
    private String test_url_apple_pay;
    @Value("${applepay.key}")
    private String key ;
    @Value("${applepay.env}")
    private String env ;
    @Resource
    JwsUtils jwsUtils;
    @Resource
    SKUService skuService;

    //沙盒
    private final String VERIFY_SANDBOX_API="https://api.storekit-sandbox.itunes.apple.com/inApps/v1/transactions/";
    //正式环境
    private final String VERIFY_PROD_API = "https://api.storekit.itunes.apple.com/inApps/v1/transactions/";
    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }



    /**
     * 苹果服务器验证
     *
     * @param receipt 账单
     * @return null 或返回结果
     * @url 要验证的地址 沙盒 https://sandbox.itunes.apple.com/verifyReceipt
     */
    public String getVerifyResult(String receipt, int type) {
        //环境判断 线上/开发环境用不同的请求链接
        try {
            String url = null;
            if (type == 0) {
                url = test_url_apple_pay; //沙盒环境,测试
            } else {
                url = url_apple_pay; //线上环境
            }
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            URL console = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "text/json");
            conn.setRequestProperty("Proxy-Connection", "Keep-Alive");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());
            //拼成固定的格式传给平台
            String str = "{\"receipt-data\":\"" + receipt + "\"" +
                    " , \"password\": \""+ key+"\" }";
            // 直接将receipt当参数发到苹果验证就行，不用拼格
            //String str = String.format(Locale.CHINA, receipt);
            hurlBufOus.write(str.getBytes());
            hurlBufOus.flush();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception ex) {
            log.error(" Apple Server verify is error " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    public boolean verify(String receipt, int type, String orderId)  {
        String verifyResult =  getVerifyResult(receipt,type);


        JSONObject jsonObject = JSONObject.parseObject(verifyResult);
        String status = jsonObject.getString("status");
        //判断是否验证成功
        if ("0".equals(status)) {
            //app端所提供的收据是有效的,验证成功
            receipt = jsonObject.getString("receipt");
            JSONObject returnJson = JSONObject.parseObject(receipt);
            String in_app = returnJson.getString("in_app");
            JSONObject in_appJson = JSONObject.parseObject(in_app.substring(1, in_app.length() - 1));
            String transactionId = in_appJson.getString("transaction_id");
            String in_app_ownership_type = in_appJson.getString("in_app_ownership_type");
            //如果验证后的订单号与app端传来的订单号一致并且状态为已支付状态则处理自己的业务
            if (orderId.equals(transactionId) && "PURCHASED".equals(in_app_ownership_type)) {
                return true;
            }
        }
        return false;
    }

    //单个交易验单
    public String verifyTransaction(String transactionId){
       String url ;
       if(env.equals("prod")){
           url = VERIFY_PROD_API +transactionId;
       }else{
           url = VERIFY_SANDBOX_API +transactionId;
       }
       Map<String,String> header = new HashMap<>();
       header.put("Authorization",iosUtils.getToken());
       log.info("token is {} ",iosUtils.getToken());
       String response =   httpClientUtils.getForObject(url,header,String.class);
       return iosUtils.verifyTransaction(response);
    }

    public void notification( RefundIosForm form) {
        String signedPayload= new String(Base64.getDecoder().decode(form.getSignedPayload().split("\\.")[0]));
        //解析苹果请求的数据
        JSONObject jsonObject=JSONObject.parseObject(signedPayload);
        ;
        Jws<Claims> result=jwsUtils.verifyAppleJWT(jsonObject.getJSONArray("x5c").get(0).toString(),form.getSignedPayload());
        log.info("------receive IosSysMsg -------");
        log.info("msg is [{}]",result.toString());
        log.info("------ finished  -------");
        String notificationType=result.getBody().get("notificationType").toString();
        Claims map=result.getBody();
        HashMap<String,Object> envmap=map.get("data",HashMap.class);
        String env=envmap.get("environment").toString();

        String resulttran= new String(Base64.getDecoder().decode(envmap.get("signedTransactionInfo").toString().split("\\.")[0]));
        JSONObject jsonObjecttran=JSONObject.parseObject(resulttran);

        Jws<Claims> result3=jwsUtils.verifyAppleJWT(jsonObjecttran.getJSONArray("x5c").get(0).toString(),envmap.get("signedTransactionInfo").toString());
        System.out.println(result3.getBody().toString());
//        HashMap<String,Object> orderMap=result3.getBody().("data",HashMap.class);
        log.info("Apple store notification type is {} ,and env is {}：",notificationType,env);

        //续费
        if(notificationType.equals("DID_RENEW")) {
            skuService.renew(AppleReceiptBO.claims2ReceiptBO(result3));
        }
        //退订
        else if(notificationType.equals("REFUND")) {
            skuService.refund(AppleReceiptBO.claims2ReceiptBO(result3));
        }
        else if(notificationType.equals("SUBSCRIBED")) {
            String oderId = result3.getBody().get("appAccountToken").toString() == null ? "" : result3.getBody().get("appAccountToken").toString();
            log.info("oderId is : "+oderId);
            skuService.pushSKU(oderId);
        }
        else if (notificationType.equals("DID_CHANGE_RENEWAL_PREF")){
            log.info("升级订阅计划");

        }
        else {
            log.info("notificationType未处理：" + notificationType);
        }

    }

}
