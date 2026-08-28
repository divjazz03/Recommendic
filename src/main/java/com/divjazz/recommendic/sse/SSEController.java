package com.divjazz.recommendic.sse;

import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class SSEController {
    private final SseManager sseManager;
    private final AuthUtils authUtils;
    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        UserDTO user = authUtils.getCurrentUser();
        return sseManager.getEmitter(user.userId());
    }

}
