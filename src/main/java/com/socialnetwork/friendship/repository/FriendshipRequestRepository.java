package com.socialnetwork.friendship.repository;

import com.socialnetwork.friendship.model.FriendshipStatus;
import com.socialnetwork.friendship.model.FriendshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long> {
    Optional<FriendshipRequest> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
    List<FriendshipRequest> findAllByReceiverIdAndStatus(UUID receiverId, FriendshipStatus status);
    @Modifying
    @Transactional
    @Query("DELETE FROM FriendshipRequest f WHERE f.senderId = :userId OR f.receiverId = :userId")
    void deleteBySenderIdOrReceiverId(@Param("userId") Long userId);
}
