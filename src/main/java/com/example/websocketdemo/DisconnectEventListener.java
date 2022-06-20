package com.example.websocketdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String profileId = hashOperations.get(sessionId, "profile-id");
        if(profileId != null) {
            valueOperations.getAndDelete(String.format("%s:%s", profileId, sessionId));
        }

        hashOperations.delete(sessionId, "page-code", "profile-id");

        log.debug("disconnected seesionId {}", sessionId);
    }
}
