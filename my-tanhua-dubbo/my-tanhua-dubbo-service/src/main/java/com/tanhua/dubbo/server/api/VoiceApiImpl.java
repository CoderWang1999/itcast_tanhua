package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.UpdateResult;
import com.tanhua.dubbo.server.pojo.Voice;
import com.tanhua.dubbo.server.service.IdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collections;
import java.util.List;

@Service(version = "1.0.0")
public class VoiceApiImpl implements  VoiceApi{
    @Autowired
    private IdService idService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void saveVoice(Voice voice) {
        voice.setVid(idService.createId("voice",voice.getId().toHexString()));
        mongoTemplate.save(voice,"voice");
    }

    @Override
    public Voice ReceivingVoice(Long userId) {
        //不是自己且状态为0
        Query query = Query.query(Criteria.where("userId").ne(userId).and("state").is(0));
        List<Voice> voices = mongoTemplate.find(query, Voice.class);
        if (CollectionUtils.isEmpty(voices)){
            return null;
        }
        Collections.shuffle(voices);
        Voice voice = voices.get(0);
        Query query1 = Query.query(Criteria.where("vid").is(voice.getVid()));
        Update update = Update.update("state",1);
        mongoTemplate.updateFirst(query1, update, "voice");
        return voice;
    }
}
