package com.socialnetwork.friendship.event.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestSentEvent {
    private UUID senderId;
    private UUID receiverId;
    private Instant createdAt;
}

