package be.kdg.ip3.checkersbackend.api.dto;

public record MakeMoveRequest(
        int fromRow,
        int fromCol,
        int toRow,
        int toCol
) {}
