package be.kdg.ip3.checkersbackend.api.dto.game;

public record MakeMoveRequest(
        int fromRow,
        int fromCol,
        int toRow,
        int toCol
) {}
