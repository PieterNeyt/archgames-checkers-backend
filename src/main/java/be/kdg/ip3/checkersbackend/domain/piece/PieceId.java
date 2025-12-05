package be.kdg.ip3.checkersbackend.domain.piece;

import org.springframework.util.Assert;

import java.util.UUID;

public record PieceId(UUID id) {
    public PieceId {
        Assert.notNull(id, "id is null");
    }
    public static PieceId create() {
        return new PieceId(UUID.randomUUID());
    }

}
