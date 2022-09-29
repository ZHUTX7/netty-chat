package com.proxy.server.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * 代理服务器配置文件读取
 */
public class ConfigService {

    private static Logger logger = LoggerFactory.getLogger(ConfigService.class);

    private static Map<String, Object> config = null;

    public Map<String, Object> readServerConfig() {
        logger.info("读取config配置------------------->>>>>>>>>>>>>>>>>>>>>>>");
        /*
        从数据库中读配置................................
         */
        Map<String, Object> loaded = null;
        try {

            InputStream in = this.getClass().getClassLoader().getResourceAsStream("proxy.yaml");
            if (in == null) {
                String filePath = "../conf/proxy.yaml";
                in = new BufferedInputStream(new FileInputStream(filePath));
            }
            Yaml yaml = new Yaml();
            loaded = (Map<String, Object>) yaml.load(in);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

      //  loaded.forEach((key,value)-> System.out.println("key:"+key+ "     "+"value:"+value));
        //System.out.println("验证-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
       // LinkedHashMap<String, Object> map = (LinkedHashMap)loaded.get("client");
      //  map.forEach((key,value)-> System.out.println("key:"+key+ "     "+"value:"+value));
      //  LinkedHashMap list = (LinkedHashMap) map.get("ztgreat");
        //list里面的子元素代表每一个URL
//        ArrayList list2 = (ArrayList)map.get("ztgreat");
//        LinkedHashMap<String, Object> map2 = (LinkedHashMap)list2.get(1);
//        map2.forEach((k,v) -> System.out.println(k+" --"+v));
//        System.out.println(map2.get("domain"));
        return config = loaded;
    }

    public synchronized Object getConfigure(String key) {
        if (config == null)
            readServerConfig();
        return config.get(key);
    }


}
