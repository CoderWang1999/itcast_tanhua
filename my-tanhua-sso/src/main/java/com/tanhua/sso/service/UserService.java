package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.enums.LogTypeEnum;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private HuanXinService huanXinService;

    /**
     * 登录操作
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 返回值
     */
    public Map<String, Object> login(String phone, String code) {
        //默认不是新用户
        boolean isNew = false;
        //得到redis中的验证码
        String redisKey = "CHECK_CODE_" + phone;
        //得到验证码
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        //校验
        if (!StringUtils.equals(code, redisData) || StringUtils.isEmpty(code)) {
            //验证码错误
            return null;
        }
        //验证码校验完成后废弃
        this.redisTemplate.delete(redisKey);
        //通过手机号在数据库中查询数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", phone);
        User user = this.userMapper.selectOne(queryWrapper);
        //判断用户是否存在
        if (null == user) {
            //不存在,需要注册该用户
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            //注册新用户
            this.userMapper.insert(user);
            isNew = true;
            //注册环信用户
            this.huanXinService.register(user.getId());
        }
        //已经登录成功
        //登录成功发送mq
        //构建消息
        Map<String, Object> message = new HashMap<>();
        //查询userId
        message.put("user_id",user.getId());
        message.put("method", LogTypeEnum.LOGIN.getValue());
        this.rocketMQTemplate.convertAndSend("tanhua-log", message);

        //生成token
        Map<String, Object> claims = new HashMap<String, Object>(1000);
        claims.put("id", user.getId());

        // 生成token
        String token = Jwts.builder()
                //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .setClaims(claims)
                //设置加密方法和加密盐
                .signWith(SignatureAlgorithm.HS256, secret)
                //设置过期时间，12小时后过期
                .setExpiration(new DateTime().plusHours(12).toDate())
                .compact();
        //返回token和isNew
        HashMap<String, Object> map = new HashMap<>(100);
        map.put("token",token);
        map.put("isNew",isNew);
        return map;


    }

    /**
     * 查询token
     *
     * @param token 用户数据
     * @return user or null
     */
    public User queryUserToken(String token) {
        try {
            // 通过token解析数据
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            User user = new User();
            user.setId(Long.valueOf(body.get("id").toString()));
            //需要返回user对象中的mobile，需要查询数据库获取到mobile数据
            //如果每次都查询数据库，必然会导致性能问题，需要对用户的手机号进行缓存操作
            //数据缓存时，需要设置过期时间，过期时间要与token的时间一致
            //如果用户修改了手机号，需要同步修改redis中的数据
            String redisKey = "TANHUA_USER_MOBILE" + user.getId();
            if (this.redisTemplate.hasKey(redisKey)) {
                //从redis中拿到手机号
                String mobile = this.redisTemplate.opsForValue().get(redisKey);
                user.setMobile(mobile);
            } else {
                //查询数据库
                User u = this.userMapper.selectById(user.getId());
                user.setMobile(u.getMobile());
                //设置过期时间
                long timeout = Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis();
                //将手机号写到redis中
                this.redisTemplate.opsForValue().set(redisKey, u.getMobile(), timeout, TimeUnit.MILLISECONDS);
            }
            return user;

        } catch (ExpiredJwtException e) {
            log.info("token已经过期! token=" + token, e);
            System.out.println("token已经过期！");
        } catch (Exception e) {
            log.error("token不合法 ! token =" + token, e);
            System.out.println("token不合法！");
        }
        return null;
    }
}

