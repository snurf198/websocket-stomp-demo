package com.example.websocketdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        log.info("disconnected");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String profileId = hashOperations.get(sessionId, "profile-id");
        if(profileId != null) {
            setOperations.remove(profileId, sessionId);
        }

        hashOperations.delete(sessionId, "page-code", "profile-id", "ip");
    }
}
