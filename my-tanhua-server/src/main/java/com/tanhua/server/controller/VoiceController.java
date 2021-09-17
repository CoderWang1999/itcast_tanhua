package com.tanhua.server.controller;

import com.tanhua.server.service.VoiceService;
import com.tanhua.server.vo.VoiceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("peachblossom")
public class VoiceController {
    @Autowired
    private VoiceService voiceService;

    /**
     * 接口名称：桃花传音-发送语音
     * 接口路径：POST/peachblossom
     */
    @PostMapping
    public ResponseEntity<Object> sendVoice(MultipartFile soundFile) throws IOException {
            return voiceService.sendVoice(soundFile);
    }

    /**
     * 接口名称：桃花传音-接收语音
     * 接口路径：GET/peachblossom
     */
    @GetMapping
    public ResponseEntity<VoiceVo> ReceivingVoice(){
        try {
            VoiceVo voiceVo = voiceService.ReceivingVoice();
            if (voiceVo != null){
                return ResponseEntity.ok(voiceVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}