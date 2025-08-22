package com.socialnetwork.friendship.kafka.producer;

import com.socialnetwork.friendship.event.FriendRequestSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFriendshipRequestEventProducer implements FriendshipRequestEventProducer {

    private final KafkaTemplate<String, FriendRequestSentEvent> kafkaTemplate;

//    @Value("${kafka.topic.friend-requests}")
    private String topic = "friend-request-events";

    @Override
    public void sendFriendRequestEvent(FriendRequestSentEvent event) {
        try {
            kafkaTemplate.send(topic, event.getSenderId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Kafka: ошибка при отправке события → {}", event, ex);
                        } else {
                            log.info("Kafka: событие отправлено → topic={}, partition={}, offset={}, event={}",
                                    topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    event);
                        }
                    });
        } catch (Exception e) {
            log.error("Kafka: критическая ошибка при публикации события → {}", event, e);
            throw new RuntimeException("Ошибка при отправке события в Kafka", e);
        }
    }
}



