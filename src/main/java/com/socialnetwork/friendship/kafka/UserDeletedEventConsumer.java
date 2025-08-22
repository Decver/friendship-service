package com.socialnetwork.friendship.kafka;

import com.socialnetwork.friendship.event.UserDeletedEvent;
import com.socialnetwork.friendship.service.FriendshipRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeletedEventConsumer {

    private final FriendshipRequestService friendshipService;

    @KafkaListener(
            topics = "user-deleted-events",
            groupId = "friendship-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserDeletedEvent event) {
        log.info("Получено событие удаления пользователя: {}", event);

        friendshipService.deleteAllFriendshipsForUser(event.getUserId());

        log.info("Удалены все дружбы для пользователя {}", event.getUserId());
    }
}
