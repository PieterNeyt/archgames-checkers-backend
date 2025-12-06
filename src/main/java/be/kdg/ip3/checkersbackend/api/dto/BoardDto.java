package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.board.Board;

public record BoardDto(
        SquareDto[][] board
) {
    public static BoardDto fromDomain(Board board) {
        SquareDto[][] squares = new SquareDto[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = SquareDto.fromDomain(board.getSquare(i, j));
            }
        }

        return new BoardDto(squares);
    }
}
