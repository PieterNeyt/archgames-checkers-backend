package be.kdg.ip3.checkersbackend.api.dto.game;

import be.kdg.ip3.checkersbackend.domain.board.Board;

import java.util.ArrayList;
import java.util.List;

public record BoardDto(
        List<List<SquareDto>> board
) {
    public static BoardDto fromDomain(Board board) {

        var rows = new ArrayList<List<SquareDto>>();

        for (int i = 0; i < 8; i++) {
            var row = new ArrayList<SquareDto>();
            for (int j = 0; j < 8; j++) {
                row.add(SquareDto.fromDomain(board.getSquare(i, j)));
            }
            rows.add(row);
        }

        return new BoardDto(rows);
    }
}
