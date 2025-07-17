package com.socialnetwork.friendship.service;


import com.socialnetwork.friendship.event.model.FriendRequestSentEvent;
import com.socialnetwork.friendship.event.publisher.FriendEventPublisher;
import com.socialnetwork.friendship.event.publisher.KafkaFriendEventPublisher;
import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.repository.FriendshipRequestRepository;
import com.socialnetwork.friendship.service.cashe.FriendCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipRequestService {

    private final FriendshipRequestRepository repository;
    private final KafkaFriendEventPublisher eventPublisher;
    private final FriendCacheService cacheService;

    /**
     * @param senderId   UUID отправителя
     * @param receiverId UUID получателя
     * @return сохранённая заявка
     */
    public FriendshipRequest sendRequest(UUID senderId, UUID receiverId) {
        log.info("Отправка заявки в друзья от {} к {}", senderId, receiverId);

        validateRequest(senderId, receiverId);

        FriendshipRequest request = new FriendshipRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus(FriendshipRequest.Status.PENDING);
        request.setCreatedAt(Instant.now());

        FriendshipRequest saved = repository.save(request);

        FriendRequestSentEvent event = new FriendRequestSentEvent(
                senderId, receiverId, saved.getCreatedAt()
        );
        eventPublisher.publishFriendRequestSent(event);

        cacheService.setPendingRequest(senderId, receiverId);

        return saved;
    }

    private void validateRequest(UUID senderId, UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Нельзя отправить заявку самому себе");
        }

        if (cacheService.isPendingRequest(senderId, receiverId)) {
            throw new IllegalStateException("Заявка уже отправлена");
        }

        repository.findBySenderIdAndReceiverId(senderId, receiverId).ifPresent(req -> {
            if (req.getStatus() == FriendshipRequest.Status.PENDING) {
                throw new IllegalStateException("Заявка уже существует");
            }
        });
    }
}
