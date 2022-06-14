package com.example.websocketdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final RedisTemplate<String, String> redisTemplate;

    @MessageMapping("/page-change")
    public void hello(
            @Payload String message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(sessionId, "page-code", message);
    }
}
