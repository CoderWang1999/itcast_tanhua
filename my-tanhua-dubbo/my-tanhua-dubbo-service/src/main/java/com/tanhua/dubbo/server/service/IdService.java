package com.tanhua.dubbo.server.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 生成自增长的id，原理：使用redis的自增长值
 * @author ZJWzxy
 */
@Service
public class IdService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Long createId(String type, String strId) {
        type = StringUtils.upperCase(type);

        String idHashKey = "TANHUA_ID_HASH_" + type;
        if (this.redisTemplate.opsForHash().hasKey(idHashKey, strId)) {
            return Long.valueOf(this.redisTemplate.opsForHash().get(idHashKey, strId).toString());
        }

        String idKey = "TANHUA_ID_" + type;
        Long id = this.redisTemplate.opsForValue().increment(idKey);

        this.redisTemplate.opsForHash().put(idHashKey, strId, id.toString());

        return id;
    }

}