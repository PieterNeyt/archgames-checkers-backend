package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import org.springframework.util.Assert;

import java.util.UUID;

public record BoardId(UUID id) {
    public BoardId {
        Assert.notNull(id, "id is null");
    }
    public static BoardId create() {
        return new BoardId(UUID.randomUUID());
    }
    public NotFoundException notFound() {
        return new NotFoundException("Board [" + id + "] not found");
    }

}
