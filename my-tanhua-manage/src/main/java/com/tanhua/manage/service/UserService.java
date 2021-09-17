package com.tanhua.manage.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.manage.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String ssoUrl;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 通过SSO的rest接口校验token
     *
     * @param token token
     * @return user
     */
    public User queryUserByToken(String token) {
        String url = ssoUrl + "/user/" + token;
        try {
            String data = this.restTemplate.getForObject(url, String.class);
            //判断data是否为空
            if (StringUtils.isEmpty(data)){
                return null;
            }
            //不为空,进行反序列化
            return MAPPER.readValue(data, User.class);
        } catch (IOException e) {
            String msg = "校验token出错 ~token " + token;
            log.error(msg, e);
        }
        return null;
    }
}
