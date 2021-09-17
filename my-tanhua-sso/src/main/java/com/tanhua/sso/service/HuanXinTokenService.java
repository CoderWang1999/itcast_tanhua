package com.tanhua.sso.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.sso.config.HuanXinConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZJWzxy
 * 拿到环信的token
 */
@Service
public class HuanXinTokenService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private HuanXinConfig huanXinConfig;

    @Autowired
    private RestTemplate restTemplate;

    public static final String REDIS_KEY = "HX_TOKEN";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String refreshToken() {
        String targetUrl = this.huanXinConfig.getUrl() + this.huanXinConfig.getOrgName() + "/" + this.huanXinConfig.getAppName() + "/token";

        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credentials");
        param.put("client_id", this.huanXinConfig.getClientId());
        param.put("client_secret", this.huanXinConfig.getClientSecret());

        //请求环信接口
        ResponseEntity<String> responseEntity =
                this.restTemplate.postForEntity(targetUrl, param, String.class);

        Integer num=200;
        if (responseEntity.getStatusCodeValue() != num) {
            return null;
        }
        String body = responseEntity.getBody();
        try {
            JsonNode jsonNode = MAPPER.readTree(body);
            String accessToken = jsonNode.get("access_token").asText();
            if (StringUtils.isNotBlank(accessToken)) {
                // 将token保存到redis，有效期为5天，环信接口返回的有效期为6天
                this.redisTemplate.opsForValue().set(REDIS_KEY, accessToken, Duration.ofDays(5));
                return accessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getToken() {
        String token = this.redisTemplate.opsForValue().get(REDIS_KEY);
        if (StringUtils.isBlank(token)) {
            return this.refreshToken();
        }
        return token;
    }
}
