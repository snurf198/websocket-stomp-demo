package com.example.websocketdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        log.info("subscribed");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String profileId;
        if(destination != null) {
            profileId = destination.replace("/sub/pf.", "");
        } else {
            profileId = null;
        }
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        String ip = headerAccessor.getSessionAttributes().get("ip").toString();

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(sessionId, "profile-id", profileId);
        hashOperations.put(sessionId, "ip", ip);

        setOperations.add(profileId, sessionId);
    }
}
