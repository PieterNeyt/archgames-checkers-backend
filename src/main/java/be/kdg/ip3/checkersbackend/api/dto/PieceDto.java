package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.piece.PieceType;

public record PieceDto(
        PieceColor color,
        PieceType type
) {
    public static PieceDto fromDomain(Piece piece) {
        return new PieceDto(
                piece.color(),
                piece.type()
        );
    }
}