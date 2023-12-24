package com.zzz.pro.service.api;

import com.alibaba.fastjson.JSONObject;
import com.zzz.pro.utils.HttpClientUtils;
import com.zzz.pro.utils.IosUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhutianxiang
 * @Description  apple api
 * @Date 2023/11/2 20:08
 * @Version 1.0
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

    //沙盒
    private final String VERIFY_TRANSACTION_API="https://api.storekit-sandbox.itunes.apple.com/inApps/v1/transactions/";
    //正式环境 https://api.storekit.itunes.apple.com/inApps/v1/transactions/{transactionId}

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

   public boolean verifyTransaction(String transactionId){
       String url = VERIFY_TRANSACTION_API +transactionId;
       Map<String,String> header = new HashMap<>();
       header.put("Authorization",iosUtils.getToken());
       String response =   httpClientUtils.getForObject(url,header,String.class);
       return iosUtils.verifyTransaction(response);
   }
}
