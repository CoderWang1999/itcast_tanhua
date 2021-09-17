package com.tanhua.manage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.manage.mapper.AdminMapper;
import com.tanhua.manage.pojo.Admin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SystemService {
    @Autowired
    AdminMapper adminMapper;
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;


    public String login(Map<String, String> param) {
        String username= param.get("username");
        String password = param.get("password");
        String uuid = param.get("uuid");
        String verificationCode = param.get("verificationCode");
        QueryWrapper<Admin>wrapper =new QueryWrapper<>();
        wrapper.eq("username",username);
        wrapper.eq("password",password);
        Admin admin = adminMapper.selectOne(wrapper);
        if (null==admin){
            return null;
        }
        //检验验证码
        String codeKey="CACHE_CODE_PREFIX" + uuid;
        String value = redisTemplate.opsForValue().get(codeKey);
        /*if (!StringUtils.equalsIgnoreCase(value,verificationCode)){
            return null;
        }*/


        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("username", admin.getUsername());
        claims.put("id", admin.getUid());

        // 生成token
        String token = Jwts.builder()
                .setClaims(claims) //设置响应数据体
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                .compact();

        try {
            // 将token存储到redis中
            String redisTokenKey = "CACHE_KEY_TOKEN" + token;

            //将密码设置为null，不参与序列化
            admin.setPassword(null);
            ObjectMapper mapper = new ObjectMapper();
            String redisTokenValue = mapper.writeValueAsString(admin);
            this.redisTemplate.opsForValue().set(redisTokenKey, redisTokenValue, Duration.ofDays(7));
            return token;
        } catch (Exception e) {
            log.error("存储token出错", e);
            return null;
        }

    }

    /*保存验证码
    * saveVerification
    * @param [uuid, code]
    */public void saveVerification(String uuid, String code) {
        //将验证码存入redis,设置有效时间为3分钟
        String key= "CACHE_CODE_PREFIX" + uuid;
        this.redisTemplate.opsForValue().set(key,code, Duration.ofMinutes(3));
    }

    public Admin find(String token) {
        //从redis中获取
        String redisTokenKey = "CACHE_KEY_TOKEN" + token;
        String data = redisTemplate.opsForValue().get(redisTokenKey);
        //反序列化
        ObjectMapper mapper =new ObjectMapper();
        try {
            Admin admin = mapper.readValue(data, Admin.class);
            return admin;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public void removeToken(String token) {
        String redisTokenKey = "CACHE_KEY_TOKEN" + token;
        redisTemplate.delete(redisTokenKey);
    }
}
