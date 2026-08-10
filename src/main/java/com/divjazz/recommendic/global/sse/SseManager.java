package com.divjazz.recommendic.global.sse;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseManager {
        private final Map<String, SseEmitter> emitters =
                new ConcurrentHashMap<>();

        public SseEmitter subscribe (String key) {
            if (emitters.containsKey(key)) {
                return emitters.get(key);
            }
            SseEmitter sseEmitter = new SseEmitter(0L);
            emitters.put(key,sseEmitter);
            sseEmitter.onCompletion(() -> emitters.remove(key));
            sseEmitter.onTimeout(() -> emitters.remove(key));
            sseEmitter.onError((err) -> emitters.remove(key));

            return sseEmitter;
        }

        public Optional<SseEmitter> getEmitter(String key) {
            return Optional.ofNullable(emitters.get(key));
        }

        public void remove(String key) {
            emitters.remove(key);
        }

        @PreDestroy
        public void shutdown() {
            emitters.values().forEach(emitter -> {
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
            });

            emitters.clear();
        }
        public void monitorEmitters () {
            log.info("Emitter count: {}", emitters.size());
        }
}
