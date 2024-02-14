package com.mindset.ameeno.service.api;

import com.mindset.ameeno.enums.ImageDataTypeEnum;
import com.mindset.ameeno.utils.HttpClientUtils;
import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author zhutianxiang
 * @Description 内容审核API
 * @Date 2023/10/16 14:56
 * @Version 1.0
 */
@Service
@Slf4j
public class ContentAnalyseService {

    private String clientId ="iKlD81IRFaoGND2oeCMXwejl";
    private String clientSecret = "Ld7uNk9kiT73SM4Gqn3dclRBh2yeRAUg";
    @Resource
    private RedisStringUtil redisStringUtil;
    @Resource
    private HttpClientUtils clientUtils;

    public  boolean ImgCensor(String imgData,Integer dataType,int imgType ) throws Exception {
        String accessToken = redisStringUtil.get("baiduToken");
        if(StringUtils.isEmpty(accessToken)){
            accessToken = getAuth(clientId,clientSecret);
        }

        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined" +
                "?access_token="
                +accessToken;
        try {
            // 创建一个RestTemplate实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("access_token", accessToken); // 替换为你的AccessToken

            System.out.printf("img URL is "+ imgData);
            // 发送Post请求
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            if(dataType == ImageDataTypeEnum.IMAGE_Base64.getCode()){
                params.add("image", imgData);
            }else{
                params.add("imgUrl", imgData);
            }

            params.add("imgType", imgType);
            // 创建HttpEntity对象，将请求体和请求头合并
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity,String.class);
            // 打印响应内容
            JSONObject jsonObject = new JSONObject(response.getBody());
            System.out.println(response.getBody());
            int isSensitive = jsonObject.getInt("conclusionType");
            if(isSensitive == 1){
                return false;
            }
            else {
                //TODO
                log.warn(response.getBody());
            }
        } catch (Exception e) {
            log.error("图片审核失败{}",e);
        }
        return true;
    }


    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public String getAuth(String ak, String sk) throws Exception {
        try {
            // 获取token地址
            String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
            String getAccessTokenUrl = authHost
                    // 1. grant_type为固定参数
                    + "grant_type=client_credentials"
                    // 2. 官网获取的 API Key
                    + "&client_id=" + ak
                    // 3. 官网获取的 Secret Key
                    + "&client_secret=" + sk;
            // 创建一个RestTemplate实例
            RestTemplate restTemplate = new RestTemplate();
            // 设置请求头
            ResponseEntity<String> response = restTemplate.getForEntity(getAccessTokenUrl,String.class);
            System.err.println("result:" + response.getBody());
            Map<String,Object> map =  JsonUtils.jsonToPojo(response.getBody(),Map.class);
            String token = (String)map.get("access_token");
            //七天过期
            if(!StringUtils.isEmpty(token)){
                redisStringUtil.set("baiduToken",token,60*60*24*7);
                return token;
            }
        } catch (Exception e) {
            log.error("获取百度 access_token失败！");
            e.printStackTrace(System.err);
        }
        return "";
    }


}
