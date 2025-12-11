package be.kdg.ip3.checkersbackend.domain;

import java.util.UUID;

public record SessionId(UUID id) {
    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

}
