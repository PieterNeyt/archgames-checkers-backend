package be.kdg.ip3.checkersbackend.domain;

import org.springframework.util.Assert;

import java.util.UUID;

public record SessionId(UUID id) {
    public SessionId {
        Assert.notNull(id, "id is null");
    }
    public static SessionId create() {
        return new SessionId(UUID.randomUUID());
    }
    public NotFoundException notFound() {
        return new NotFoundException("Game [" + id + "] not found");
    }
}
