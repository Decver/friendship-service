package com.socialnetwork.friendship.controller;

import com.socialnetwork.friendship.dto.Request.SendFriendRequestDto;
import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.service.FriendshipRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipRequestService friendshipRequestService;

    @PostMapping("/requests")
    public ResponseEntity<FriendshipRequest> sendRequest(
            @Valid @RequestBody SendFriendRequestDto dto
    ) {
        FriendshipRequest result = friendshipRequestService.sendRequest(dto.senderId(), dto.receiverId());
        return ResponseEntity.ok(result);
    }
}
