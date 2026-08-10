package com.divjazz.recommendic.video;

import io.getstream.services.framework.StreamSDKClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StreamVideoService {
    private final StreamSDKClient streamSDKClient;
    public String createVideoCallToken(String userId){
        return streamSDKClient.tokenBuilder().createToken(userId);
    }
    public String getApiKey() {
        return streamSDKClient.getHttpClient().getApiKey();
    }
}
