package be.kdg.ip3.checkersbackend.domain.game;

import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import org.springframework.util.Assert;

import java.util.UUID;

public record GameId(UUID id) {
    public GameId {
        Assert.notNull(id, "id is null");
    }
    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }
    public NotFoundException notFound() {
        return new NotFoundException("Game [" + id + "] not found");
    }
}
