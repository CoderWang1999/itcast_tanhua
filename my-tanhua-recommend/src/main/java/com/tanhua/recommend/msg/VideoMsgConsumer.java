package com.tanhua.recommend.msg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.recommend.enums.LogTypeEnum;
import com.tanhua.recommend.pojo.RecommendVideo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "tanhua-video",
        consumerGroup = "tanhua-video-consumer")
public class VideoMsgConsumer implements RocketMQListener<String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoMsgConsumer.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);

            Long userId = jsonNode.get("userId").asLong();
            Long vid = jsonNode.get("vid").asLong();
            Integer type = jsonNode.get("type").asInt();

            //1-发动态，2-点赞， 3-取消点赞，4-评论
            RecommendVideo recommendVideo = new RecommendVideo();
            recommendVideo.setUserId(userId);
            recommendVideo.setId(ObjectId.get());
            recommendVideo.setDate(System.currentTimeMillis());
            recommendVideo.setVideoId(vid);

            switch (type) {
                case 1: {
                    recommendVideo.setScore(2d);
                    //构建消息
                    Map<String, Object> message = new HashMap<>();
                    //查询userId
                    message.put("user_id",userId);
                    message.put("method", LogTypeEnum.VIDEO_ADD.getValue());
                    this.rocketMQTemplate.convertAndSend("tanhua-log", message);
                    break;
                }
                case 2: {
                    recommendVideo.setScore(5d);
                    //构建消息
                    Map<String, Object> message = new HashMap<>();
                    //查询userId
                    message.put("user_id",userId);
                    message.put("method", LogTypeEnum.VIDEO_LIKE.getValue());
                    this.rocketMQTemplate.convertAndSend("tanhua-log", message);
                    break;
                }
                case 3: {
                    recommendVideo.setScore(-5d);
                    //构建消息
                    Map<String, Object> message = new HashMap<>();
                    //查询userId
                    message.put("user_id",userId);
                    message.put("method", LogTypeEnum.VIDEO_UNLIKE.getValue());
                    this.rocketMQTemplate.convertAndSend("tanhua-log", message);
                    break;
                }
                case 4: {
                    recommendVideo.setScore(10d);
                    //构建消息
                    Map<String, Object> message = new HashMap<>();
                    //查询userId
                    message.put("user_id",userId);
                    message.put("method", LogTypeEnum.VIDEO_COMMENT.getValue());
                    this.rocketMQTemplate.convertAndSend("tanhua-log", message);
                    break;
                }
                default: {
                    recommendVideo.setScore(0d);
                    break;
                }
            }

            String collectionName = "recommend_video_" + new DateTime().toString("yyyyMMdd");
            this.mongoTemplate.save(recommendVideo, collectionName);

        } catch (Exception e) {
            LOGGER.error("处理小视频消息失败~" + msg, e);
        }
    }
}
