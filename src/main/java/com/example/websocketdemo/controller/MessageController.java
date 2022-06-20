package com.example.websocketdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final RedisTemplate<String, String> redisTemplate;

    @MessageMapping("/page-change")
    public void hello(
            @Payload String message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
//
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(sessionId, "page-code", message);
        log.debug("sessionId {}", sessionId);
        log.debug("message {} sent", message);
    }
}
