package com.example.websocketdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String key = new String(message.getBody());
        log.debug("expired key: {}", key);
    }
}
