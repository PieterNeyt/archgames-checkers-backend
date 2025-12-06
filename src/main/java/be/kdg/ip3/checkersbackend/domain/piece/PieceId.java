package be.kdg.ip3.checkersbackend.domain.piece;

import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import org.springframework.util.Assert;

import java.util.UUID;

public record PieceId(UUID id) {
    public PieceId {
        Assert.notNull(id, "id is null");
    }
    public static PieceId create() {
        return new PieceId(UUID.randomUUID());
    }
    public NotFoundException notFound() {
        return new NotFoundException("Piece [" + id + "] not found");
    }
}
