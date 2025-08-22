package com.socialnetwork.friendship.service;

import com.socialnetwork.friendship.kafka.producer.FriendshipRequestEventProducer;
import com.socialnetwork.friendship.model.FriendshipRequest;
import com.socialnetwork.friendship.model.FriendshipStatus;
import com.socialnetwork.friendship.repository.FriendshipRequestRepository;
import com.socialnetwork.friendship.service.cashe.RedisFriendCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendshipRequestServiceTest {

    @Mock
    private FriendshipRequestRepository repository;

    @Mock
    private RedisFriendCacheService redisCache;

    @Mock
    private FriendshipRequestEventProducer eventProducer;

    @InjectMocks
    private FriendshipRequestService service;

    private UUID senderId;
    private UUID receiverId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
    }

    @Test
    void sendFriendRequest_success() {
        FriendshipRequest saved = new FriendshipRequest();
        saved.setId(1L);
        saved.setSenderId(senderId);
        saved.setReceiverId(receiverId);
        saved.setStatus(FriendshipStatus.PENDING);
        saved.setCreatedAt(Instant.now());

        when(repository.findBySenderIdAndReceiverId(senderId, receiverId))
                .thenReturn(Optional.empty());
        when(repository.save(any(FriendshipRequest.class))).thenReturn(saved);

        FriendshipRequest result = service.sendFriendRequest(senderId, receiverId);

        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        verify(repository).save(any(FriendshipRequest.class));
        verify(redisCache).addPendingRequest(receiverId, saved);
        verify(eventProducer).sendFriendRequestEvent(any());
    }

    @Test
    void sendFriendRequest_selfRequest_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.sendFriendRequest(senderId, senderId));
        verifyNoInteractions(repository, redisCache, eventProducer);
    }

    @Test
    void sendFriendRequest_alreadyExists_returnsExisting() {
        FriendshipRequest existing = new FriendshipRequest();
        existing.setSenderId(senderId);
        existing.setReceiverId(receiverId);

        when(repository.findBySenderIdAndReceiverId(senderId, receiverId))
                .thenReturn(Optional.of(existing));

        FriendshipRequest result = service.sendFriendRequest(senderId, receiverId);

        assertSame(existing, result);
        verify(repository, never()).save(any());
        verifyNoInteractions(redisCache, eventProducer);
    }

    @Test
    void respondToFriendRequest_accept_success() {
        FriendshipRequest request = new FriendshipRequest();
        request.setId(1L);
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus(FriendshipStatus.PENDING);
        request.setCreatedAt(Instant.now());

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FriendshipRequest result = service.respondToFriendRequest(1L, true);

        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
        verify(redisCache).saveFriends(senderId, receiverId);
        verify(eventProducer).sendFriendRequestEvent(any());
    }

    @Test
    void respondToFriendRequest_reject_success() {
        FriendshipRequest request = new FriendshipRequest();
        request.setId(1L);
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setStatus(FriendshipStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FriendshipRequest result = service.respondToFriendRequest(1L, false);

        assertEquals(FriendshipStatus.REJECTED, result.getStatus());
        verify(redisCache).removePendingRequest(receiverId, 1L);
        verify(eventProducer).sendFriendRequestEvent(any());
    }

    @Test
    void respondToFriendRequest_notFound_throwsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.respondToFriendRequest(999L, true));
    }

    @Test
    void respondToFriendRequest_alreadyProcessed_throwsException() {
        FriendshipRequest request = new FriendshipRequest();
        request.setId(1L);
        request.setStatus(FriendshipStatus.ACCEPTED);

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class,
                () -> service.respondToFriendRequest(1L, true));
    }

    @Test
    void deleteAllFriendshipsForUser_success() {
        service.deleteAllFriendshipsForUser(42L);

        verify(repository).deleteBySenderIdOrReceiverId(42L);
        verify(redisCache).removeUserFromCache(42L);
    }
}

