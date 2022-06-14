package com.example.websocketdemo.web.v1.api;

import com.example.websocketdemo.web.v1.model.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TabletConnectionController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/friends/v1/tablet-sessions")
    public List<ResponseDto> getTabletSessions(
            @RequestParam List<String> profileIds
    ) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        List<String> sessionIds = new ArrayList<>();

        profileIds.forEach(id -> {
            Set<String> members = setOperations.members(id);
            if(members != null) {
                sessionIds.addAll(members);
            }
        });

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        return sessionIds.stream().map(id -> {
            String pageCode = hashOperations.get(id, "page-code");
            String profileId = hashOperations.get(id, "profile-id");
            String ip = hashOperations.get(id, "ip");

            return new ResponseDto().setPageCode(pageCode).setProfileId(profileId).setIp(ip).setSessionId(id);
        }).collect(Collectors.toList());
    }
}
