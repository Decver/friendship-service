package com.socialnetwork.friendship.service.cashe;

import java.util.UUID;

public interface FriendCacheService {
    void setPendingRequest(UUID senderId, UUID receiverId);
    boolean isPendingRequest(UUID senderId, UUID receiverId);
}
