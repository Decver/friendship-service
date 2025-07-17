package com.socialnetwork.friendship.model;

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
    private Status status;

    private Instant createdAt;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}
