package com.socialnetwork.friendship.event.publisher;

import com.socialnetwork.friendship.event.model.FriendRequestSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaFriendEventPublisher implements FriendEventPublisher {

    public KafkaFriendEventPublisher(KafkaTemplate<String, FriendRequestSentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private final KafkaTemplate<String, FriendRequestSentEvent> kafkaTemplate;

//    @Value("${kafka.topic.friend-requests}")
    private String topic = "friend-request-events";

    @Override
    public void publishFriendRequestSent(FriendRequestSentEvent event) {
        kafkaTemplate.send(topic, event.getSenderId().toString(), event);
        log.info("📤 Kafka: отправлено событие friend-request → {}", event);
    }
}

