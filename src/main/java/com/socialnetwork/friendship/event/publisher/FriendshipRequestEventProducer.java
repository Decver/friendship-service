package com.socialnetwork.friendship.event.publisher;

import com.socialnetwork.friendship.event.model.FriendRequestSentEvent;

public interface FriendshipRequestEventProducer {
    void sendFriendRequestEvent(FriendRequestSentEvent event);
}
