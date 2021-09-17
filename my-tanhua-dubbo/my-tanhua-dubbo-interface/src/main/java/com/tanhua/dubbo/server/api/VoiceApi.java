package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Voice;

public interface VoiceApi {

    void saveVoice(Voice voice);

    Voice ReceivingVoice(Long id);
}
