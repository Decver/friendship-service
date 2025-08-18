package com.socialnetwork.friendship.model;

import com.socialnetwork.friendship.event.model.FriendshipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendship_requests", uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
@Data
public class FriendshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID senderId;
    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    private Instant createdAt;
}
