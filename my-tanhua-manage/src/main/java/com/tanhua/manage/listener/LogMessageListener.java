package com.tanhua.manage.listener;

import java.io.IOException;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.manage.pojo.Log;
import com.tanhua.manage.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息监听器，监听日志消息
 */
@Component
@RocketMQMessageListener(topic = "tanhua-log",consumerGroup = "tanhua-log-consumer")
@Slf4j
public class LogMessageListener implements RocketMQListener<String> {
    @Autowired
    private LogService logService;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public void onMessage(String message) {
        //log.info("监听器中消息内容：" + message);
        //1. 日志的json字符串转换为map对象
        //Map<String,String> map = JSON.parseObject(message, Map.class);
        Log log = null;
        try {
            JsonNode jsonNode = MAPPER.readTree(message);
            //2. 获取数据
            int userId = jsonNode.get("user_id").asInt();
            String method = jsonNode.get("method").asText();
            //3. 创建日志对象，封装数据
            log = new Log();
            log.setUserId(userId);
            log.setMethod(method);
            log.setActiveTime(new Date());
            log.setEquipment("手机");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4. 保存
        logService.save(log);
    }
}