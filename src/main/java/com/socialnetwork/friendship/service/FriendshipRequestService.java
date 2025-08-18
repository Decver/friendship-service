package com.socialnetwork.friendship.service;


import com.socialnetwork.friendship.event.model.FriendRequestSentEvent;
import com.socialnetwork.friendship.event.model.FriendshipStatus;
import com.socialnetwork.friendship.event.publisher.FriendshipRequestEventProducer;
import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.repository.FriendshipRequestRepository;
import com.socialnetwork.friendship.service.cashe.RedisFriendCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipRequestService {

    private final FriendshipRequestRepository friendshipRequestRepository;
    private final RedisFriendCacheService redisFriendCacheService;
    private final FriendshipRequestEventProducer friendshipRequestEventProducer;

    @Transactional
    public FriendshipRequest sendFriendRequest(UUID senderId, UUID receiverId) {
        log.info("Новый запрос дружбы: {} → {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Нельзя отправить запрос самому себе");
        }

        Optional<FriendshipRequest> existing = friendshipRequestRepository
                .findBySenderIdAndReceiverId(senderId, receiverId);

        if (existing.isPresent()) {
            log.warn("Запрос дружбы уже существует: {} → {}", senderId, receiverId);
            return existing.get();
        }

        FriendshipRequest request = new FriendshipRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus(FriendshipStatus.PENDING);
        request.setCreatedAt(Instant.now());

        FriendshipRequest saved = friendshipRequestRepository.save(request);
        log.info("✅ Запрос дружбы сохранён: {}", saved);

        redisFriendCacheService.addPendingRequest(receiverId, saved);

        FriendRequestSentEvent event = new FriendRequestSentEvent(
                saved.getSenderId(),
                saved.getReceiverId(),
                saved.getCreatedAt()
        );
        friendshipRequestEventProducer.sendFriendRequestEvent(event);

        return saved;
    }

    @Transactional
    public FriendshipRequest respondToFriendRequest(Long requestId, boolean accept) {
        FriendshipRequest request = friendshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));

        if (request.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Запрос уже обработан");
        }

        request.setStatus(accept ? FriendshipStatus.ACCEPTED : FriendshipStatus.REJECTED);
        FriendshipRequest updated = friendshipRequestRepository.save(request);

                if (accept) {
            redisFriendCacheService.saveFriends(request.getSenderId(), request.getReceiverId());
        } else {
            redisFriendCacheService.removePendingRequest(request.getReceiverId(), requestId);
        }

        FriendRequestSentEvent event = new FriendRequestSentEvent(
                updated.getSenderId(),
                updated.getReceiverId(),
                updated.getCreatedAt()
        );
        friendshipRequestEventProducer.sendFriendRequestEvent(event);

        log.info("Ответ на запрос дружбы: id={}, статус={}", requestId, updated.getStatus());
        return updated;
    }
}


