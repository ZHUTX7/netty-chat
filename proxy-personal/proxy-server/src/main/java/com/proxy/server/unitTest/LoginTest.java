//package com.proxy.server.unitTest;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.sql.ResultSet;
//
///**
// * @author : admin`
// * @version :V1.0`
// * @description :
// * @update : 2021/2/22 17:15
// */
//public class LoginTest {
//
//    public static void main(String[] args) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        String json = "{\n" +
//                "\t\"user_name\":\"admin\",\n" +
//                "\t\"password\":\"admin\"\n" +
//                "}";
//        HttpEntity<String> request = new HttpEntity<>(json,headers);
//
//        String url = "http://localhost:9004/verify/login";
//        ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, request, String.class);
//
//        String body = postForEntity.getBody();
//
//        System.out.println(body);
//    }
//}
