package com.socialnetwork.friendship.service.cashe;

import com.socialnetwork.friendship.model.FriendshipRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisFriendCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String keyPending(UUID userId) {
        return "friends:pending:" + userId;
    }

    private String keyFriends(UUID userId) {
        return "friends:list:" + userId;
    }

    // Добавление запроса в pending
    public void addPendingRequest(UUID receiverId, FriendshipRequest request) {
        redisTemplate.opsForList().rightPush(keyPending(receiverId), request);
        log.info("💾 Redis: добавлен pending запрос для {}", receiverId);
    }

    // Удаление запроса из pending
    public void removePendingRequest(UUID receiverId, Long requestId) {
        redisTemplate.opsForList().remove(keyPending(receiverId), 1, requestId);
        log.info("🗑 Redis: удалён запрос {} из pending для {}", requestId, receiverId);
    }

    // Сохранение дружбы
    public void saveFriends(UUID user1, UUID user2) {
        redisTemplate.opsForSet().add(keyFriends(user1), user2.toString());
        redisTemplate.opsForSet().add(keyFriends(user2), user1.toString());
        log.info("🤝 Redis: сохранена дружба {} ↔ {}", user1, user2);
    }
}



