package com.tanhua.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.server.utils.Cache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Component
public class RedisCacheInterceptor implements HandlerInterceptor {

    @Value("${tanhua.cache.enable}")
    private Boolean enable;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //缓存的全局开关校验
        if (!enable) {
            //没有开启
            return true;
        }
        //校验handle是否是HandlerMethod---不是rest请求,基本就是静态资源,放行
        if (!(handler instanceof HandlerMethod)) {
            //不是
            return true;

        }
        //判断handle中是否是get请求
        if (!((HandlerMethod) handler).hasMethodAnnotation(GetMapping.class)) {
            //不是get请求
            return true;
        }
        //判断handle中是否有cache注解
        if (!((HandlerMethod) handler).hasMethodAnnotation(Cache.class)) {
            //没有cache
            return true;
        }
        //缓存命中
        String redisKey=createRedisKey(request);
        String cacheData = this.redisTemplate.opsForValue().get(redisKey);
        //判断
        if (StringUtils.isEmpty(cacheData)){
            //缓存没有命中
            return true;
        }
        //缓存命中,将data数据进行响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(cacheData);
        return false;
    }

    /**
     * 生成redis中的key,规则:SERVER_CACHE_DATA_MD5 (url+param+token)
     *
     * @param request 请求
     * @return redisKey
     */
    public static String createRedisKey(HttpServletRequest request) throws Exception {
        //拿到三个参数
        String url = request.getRequestURI();
        String param = MAPPER.writeValueAsString(request.getParameterMap());
        String token = request.getHeader("Authorization");
        String data = url + "_" + param + "_" + token;
        return "SERVER_CACHE_DATA_" + DigestUtils.md5Hex(data);
    }
}
