package be.kdg.ip3.checkersbackend.domain.board;

import org.springframework.util.Assert;

import java.util.UUID;

public record BoardId(UUID id) {
    public BoardId {
        Assert.notNull(id, "id is null");
    }
    public static BoardId create() {
        return new BoardId(UUID.randomUUID());
    }

}
