package com.proxy.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : admin
 * @version :V1.0
 * @description :
 * @update : 2021/2/23 10:48
 */
public class RpcUtils {
    private static Logger logger = LoggerFactory.getLogger(RpcUtils.class);
    public static JSONObject callOtherInterface(JSONObject jsonParam, String url) {
        HttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        //部署则添加下行
        //ip = "192.168.3.199";

        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(jsonParam.toString(), "UTF-8");
            //s.setContentEncoding("UTF-8");//此处测试发现不能单独设置字符串实体的编码，否则出错！应该在创建对象时创建
            s.setContentType("application/json");
            post.setEntity(s);
            post.addHeader("content-type", "application/json;charset=UTF-8");
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                jsonObject = JSONObject.parseObject(EntityUtils.toString(res.getEntity()));

            }
        } catch (Exception e) {
            logger.error("服务间接口调用出错,JSON获取失败！");
            logger.error(e.getMessage());
        }
        return jsonObject;
    }
    public static JSONObject callOtherInterface(JSONObject jsonParam, String ip, String port, String postUrl) {
        HttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        //部署则添加下行
        //ip = "192.168.3.199";
        String url = "http://"+ip+":" + port + postUrl;
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(jsonParam.toString(), "UTF-8");
            //s.setContentEncoding("UTF-8");//此处测试发现不能单独设置字符串实体的编码，否则出错！应该在创建对象时创建
            s.setContentType("application/json");
            post.setEntity(s);
            post.addHeader("content-type", "application/json;charset=UTF-8");
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回json格式：
                jsonObject = JSONObject.parseObject(EntityUtils.toString(res.getEntity()));

            }
        } catch (Exception e) {
            logger.error("服务间接口调用出错,JSON获取失败！");
            logger.error(e.getMessage());
        }
        return jsonObject;
    }
}
