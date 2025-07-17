package com.socialnetwork.friendship.event.publisher;

import com.socialnetwork.friendship.event.model.FriendRequestSentEvent;

public interface FriendEventPublisher {
    void publishFriendRequestSent(FriendRequestSentEvent event);
}
