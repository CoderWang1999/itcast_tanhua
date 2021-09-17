package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.dubbo.server.api.VoiceApi;
import com.tanhua.dubbo.server.pojo.Voice;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.VoiceVo;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;


@Service
public class VoiceService {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Reference(version = "1.0.0")
    private VoiceApi voiceApi;

    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    public ResponseEntity<Object> sendVoice(MultipartFile soundFile) {
        //获取userInfo
        User user = UserThreadLocal.get();
        UserInfo userInfo = userInfoService.queryUserInfoByUserId(user.getId());
        Voice voice = new Voice();
        voice.setAge(userInfo.getAge());
        voice.setAvatar(userInfo.getLogo());
        voice.setNickname(userInfo.getNickName());
        voice.setUserId(user.getId());
        voice.setGender(StringUtils.equalsIgnoreCase(userInfo.getSex().toString(), "1") ? "man" : "woman");
        //上传到fastDFS
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadFile(soundFile.getInputStream(),
                    soundFile.getSize(),
                    StringUtils.substringAfter(soundFile.getOriginalFilename(), "."),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        voice.setState(0);
        voice.setCreated(System.currentTimeMillis());
        voice.setId(new ObjectId());
        voice.setSoundUrl(fdfsWebServer.getWebServerUrl() + "/" + storePath.getFullPath());
        try {
            voiceApi.saveVoice(voice);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public VoiceVo ReceivingVoice() {
        User user = UserThreadLocal.get();
        VoiceVo voiceVo = new VoiceVo();
       Voice voice =  voiceApi.ReceivingVoice(user.getId());
        voiceVo.setAge(voice.getAge());
        voiceVo.setGender(voice.getGender());
        voiceVo.setAvatar(voice.getAvatar());
        voiceVo.setSoundUrl(voice.getSoundUrl());
        voiceVo.setId(Integer.parseInt(voice.getUserId().toString()));
        voiceVo.setNickname(voice.getNickname());
        //设置收取语音次数
       String redisKey = "receiv_voice_" + user.getId();
       if (!redisTemplate.hasKey(redisKey)){
           redisTemplate.opsForValue().set(redisKey,"10",1L, TimeUnit.DAYS);
       }

       if (Integer.parseInt(redisTemplate.opsForValue().get(redisKey)) == 0){
                    return null;
           }
           //接收一次后自减
        Long count = redisTemplate.opsForValue().decrement(redisKey);
       voiceVo.setRemainingTimes(Integer.parseInt(count.toString()));
        return voiceVo;
    }
}
