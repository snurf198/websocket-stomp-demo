package com.example.websocketdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String profileId;
        if(destination != null && destination.contains("/exchange/noti/pf.")) {
            profileId = destination.replace("/exchange/noti/pf.", "");
        } else {
            profileId = null;
        }

        String sessionId = headerAccessor.getSessionId();

        log.debug("subscribed with sessionId {} with profileId {}", sessionId, profileId);

        if(profileId != null && sessionId != null) {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

            hashOperations.put(sessionId, "profile-id", profileId);
            redisTemplate.expire(sessionId, 30, TimeUnit.SECONDS);

            valueOperations.set(String.format("%s:%s", profileId, sessionId), "");
            redisTemplate.expire(String.format("%s:%s", profileId, sessionId), 30, TimeUnit.SECONDS);
        }
    }
}
