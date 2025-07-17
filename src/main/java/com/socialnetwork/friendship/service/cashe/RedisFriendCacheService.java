package com.socialnetwork.friendship.service.cashe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisFriendCacheService implements FriendCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private String key(UUID senderId, UUID receiverId) {
        return "friend:pending:" + senderId + ":" + receiverId;
    }

    @Override
    public void setPendingRequest(UUID senderId, UUID receiverId) {
        redisTemplate.opsForValue().set(key(senderId, receiverId), "1", Duration.ofMinutes(10));
    }

    @Override
    public boolean isPendingRequest(UUID senderId, UUID receiverId) {
        return redisTemplate.hasKey(key(senderId, receiverId));
    }
}

