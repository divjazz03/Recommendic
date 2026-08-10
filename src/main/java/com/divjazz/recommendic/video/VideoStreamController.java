package com.divjazz.recommendic.video;

import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.divjazz.recommendic.global.RequestUtils.getResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
public class VideoStreamController {

    private final StreamVideoService streamVideoService;
    private final AuthUtils authUtils;
    @GetMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public Response<Map<String, String>> generateToken() {
        String userId = authUtils.getCurrentUser().userId();
        log.info("Authenticated UserId {}", userId);
        String token = streamVideoService.createVideoCallToken(userId);
        return getResponse(Map.of("token", token), HttpStatus.OK);
    }
}
