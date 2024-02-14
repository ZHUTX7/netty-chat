package com.mindset.ameeno.utils;

/**
 * @Author zhutianxiang
 * @Description httpClient
 * @Version 1.0
 */

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import javax.annotation.Resource;
import java.util.Map;
/**
 * @Author zhutianxiang
 * @Description HttpClientUtils
 * @Date 2023/11/1 19:32
 * @Version 1.0
 */
@Component
public class HttpClientUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送GET请求
     *
     * @param url      请求URL地址
     * @param headers  请求头信息
     * @param clazz    响应对象类型
     * @param <T>      响应对象泛型
     * @return 返回结果
     */
    public <T> T getForObject(String url, Map<String, String> headers, Class<T> clazz)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, request, clazz);
        return response.getBody();
    }

    /**
     * 发送POST请求
     *
     * @param url      请求URL地址
     * @param headers  请求头信息
     * @param body     请求体
     * @param clazz    响应对象类型
     * @param <T>      响应对象泛型
     * @return 返回结果
     */
    public <T> T postForObject(String url, Map<String, String> headers, Object body, Class<T> clazz) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<Object> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, request, clazz);
        return response.getBody();
    }

    /**
     * 发送POST请求
     *
     * @param url      请求URL地址
     * @param headers  请求头信息
     * @param body     请求体
     * @param typeRef  响应对象类型
     * @param <T>      响应对象泛型
     * @return 返回结果
     */
    public <T> T postForObject(String url, Map<String, String> headers, Object body, ParameterizedTypeReference<T> typeRef) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<Object> request = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, request, typeRef);
        return response.getBody();
    }
}
