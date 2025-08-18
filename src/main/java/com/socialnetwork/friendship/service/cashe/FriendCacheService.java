package com.socialnetwork.friendship.service.cashe;

import java.util.Set;
import java.util.UUID;

public interface FriendCacheService {

    // pending (анти-дубликаты и быстрые проверки)
    void setPendingRequest(UUID senderId, UUID receiverId);
    boolean isPendingRequest(UUID senderId, UUID receiverId);
    void removePendingRequest(UUID senderId, UUID receiverId);

    // трекинг входящих/исходящих заявок (удобно для UI)
    void addOutgoing(UUID senderId, UUID receiverId);
    void addIncoming(UUID receiverId, UUID senderId);
    void removeOutgoing(UUID senderId, UUID receiverId);
    void removeIncoming(UUID receiverId, UUID senderId);

    Set<UUID> getOutgoing(UUID userId);
    Set<UUID> getIncoming(UUID userId);

    // дружба
    void saveFriends(UUID userId, UUID friendId);
    boolean areFriends(UUID userId, UUID friendId);
    Set<UUID> getFriends(UUID userId);
    void removeFriends(UUID userId, UUID friendId);

    // опционально: тёплый старт/перестройка кеша из БД
    void replaceFriends(UUID userId, Set<UUID> friends);
}


