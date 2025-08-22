package com.socialnetwork.friendship.service;

import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.service.cashe.RedisFriendCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RedisFriendCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOps;

    @Mock
    private SetOperations<String, Object> setOps;

    @InjectMocks
    private RedisFriendCacheService redisFriendCacheService;

    private UUID user1;
    private UUID user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = UUID.randomUUID();
        user2 = UUID.randomUUID();

        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
    }

    @Test
    void testAddPendingRequest() {
        FriendshipRequest request = new FriendshipRequest();

        redisFriendCacheService.addPendingRequest(user1, request);

        verify(listOps, times(1)).rightPush("friends:pending:" + user1, request);
    }

    @Test
    void testRemovePendingRequest() {
        Long requestId = 123L;

        redisFriendCacheService.removePendingRequest(user1, requestId);

        verify(listOps, times(1)).remove("friends:pending:" + user1, 1, requestId);
    }

    @Test
    void testSaveFriends() {
        redisFriendCacheService.saveFriends(user1, user2);

        verify(setOps, times(1)).add("friends:list:" + user1, user2.toString());
        verify(setOps, times(1)).add("friends:list:" + user2, user1.toString());
    }

    @Test
    void testRemoveUserFromCache() {
        Long userId = 42L;

        redisFriendCacheService.removeUserFromCache(userId);

        verify(redisTemplate, times(1)).delete("friends:" + userId);
        verify(redisTemplate, times(1)).delete("pending:" + userId);
    }
}

