package com.socialnetwork.friendship.dto.Request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendFriendRequestDto(
        @NotNull UUID senderId,
        @NotNull UUID receiverId
) {}

