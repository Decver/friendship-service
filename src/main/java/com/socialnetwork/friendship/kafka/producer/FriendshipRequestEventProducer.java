package com.socialnetwork.friendship.kafka.producer;

import com.socialnetwork.friendship.event.FriendRequestSentEvent;

public interface FriendshipRequestEventProducer {
    void sendFriendRequestEvent(FriendRequestSentEvent event);
}
