package com.socialnetwork.friendship.repository;

import com.socialnetwork.friendship.model.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {
    Optional<FriendshipRequest> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
