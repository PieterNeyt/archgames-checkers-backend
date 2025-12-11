package be.kdg.ip3.checkersbackend.api.dto.game;

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
                square.row(),
                square.col(),
                square.color(),
                square.piece() != null ? PieceDto.fromDomain(square.piece()) : null
        );
    }
}