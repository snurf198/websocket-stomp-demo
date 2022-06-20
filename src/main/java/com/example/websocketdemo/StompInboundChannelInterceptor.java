package com.example.websocketdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompInboundChannelInterceptor implements ChannelInterceptor {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        String sessionId = headerAccessor.getSessionId();
        boolean heartbeat = headerAccessor.isHeartbeat();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String profileId = hashOperations.get(sessionId, "profile-id");

        if(heartbeat) {
            redisTemplate.expire(sessionId, 30, TimeUnit.SECONDS);
            redisTemplate.expire(String.format("%s:%s", profileId, sessionId), 30, TimeUnit.SECONDS);
        }

        log.debug("Is HeartBeat {} sessionId {} profileId {}", heartbeat, sessionId, profileId);

        return message;
    }
}
