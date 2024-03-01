package com.mindset.ameeno.pojo.dto;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author zhutianxiang
 
 * @Date 2023/12/10 16:36
 * @Version 1.0
 */
@Data
@Table(name = "app_config")
public class AppConfig {
    @Id
    private String id;
    private String appEnv;
    private String appName;
    private String payMode;
}
