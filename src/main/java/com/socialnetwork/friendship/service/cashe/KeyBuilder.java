package com.socialnetwork.friendship.service.cashe;

import java.time.Duration;
import java.util.UUID;

public final class KeyBuilder {
    private KeyBuilder() {}
    public static final Duration PENDING_TTL = Duration.ofMinutes(10);
    public static final Duration REQUESTS_TTL = Duration.ofHours(1); // входящие/исходящие

    public static String pending(UUID s, UUID r) { return "friend:pending:" + s + ':' + r;}
    public static String friends(UUID u) { return "friend:list:" + u;}
    public static String outgoing(UUID u) { return "friend:out:" + u;}
    public static String incoming(UUID u) { return "friend:in:" + u;}
}
