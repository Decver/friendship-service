package com.socialnetwork.friendship.controller;

import com.socialnetwork.friendship.dto.Request.SendFriendRequestDto;
import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.service.FriendshipRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/friends/requests")
@RequiredArgsConstructor
public class FriendshipRequestController {

    private final FriendshipRequestService friendshipRequestService;

    /**
     * 📩 Отправить запрос дружбы
     */
    @PostMapping
    public ResponseEntity<FriendshipRequest> sendFriendRequest(
            @RequestParam UUID senderId,
            @RequestParam UUID receiverId) {

        FriendshipRequest request = friendshipRequestService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok(request);
    }

    /**
     * ✅ Принять или ❌ Отклонить запрос
     */
    @PostMapping("/{requestId}/respond")
    public ResponseEntity<FriendshipRequest> respondToFriendRequest(
            @PathVariable Long requestId,
            @RequestParam boolean accept) {

        FriendshipRequest request = friendshipRequestService.respondToFriendRequest(requestId, accept);
        return ResponseEntity.ok(request);
    }
}

