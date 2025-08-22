package com.socialnetwork.friendship.kafka.Listener;

import com.socialnetwork.friendship.event.FriendRequestSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestEventListener {
    @KafkaListener(topics = "friend-request-events", groupId = "notification-service")
    public void handleFriendRequest(FriendRequestSentEvent event) {
        log.info("📥 Kafka: получено событие friend-request → {}", event);
    }
}
