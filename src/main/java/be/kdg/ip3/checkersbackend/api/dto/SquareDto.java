package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.board.SquareColor;

public record SquareDto(
        int row,
        int col,
        SquareColor color,
        PieceDto piece
) {
    public static SquareDto fromDomain(Square square) {
        return new SquareDto(
                square.getRow(),
                square.getCol(),
                square.getColor(),
                square.getPiece() != null ? PieceDto.fromDomain(square.getPiece()) : null
        );
    }
}