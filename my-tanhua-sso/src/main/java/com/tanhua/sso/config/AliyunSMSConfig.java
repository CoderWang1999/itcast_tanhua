package com.tanhua.sso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@Configuration
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class AliyunSMSConfig {

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String domain;
    private String signName;
    private String templateCode;
}