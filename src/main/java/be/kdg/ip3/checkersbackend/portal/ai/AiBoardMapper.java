package be.kdg.ip3.checkersbackend.portal.ai;

import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;

public final class AiBoardMapper {

    private AiBoardMapper() {}

    public static String[][] toAiBoard(Board board) {
        String[][] aiBoard = new String[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Square square = board.getSquares()[7 - row][col];

                if (square.isEmpty() || !isPlayableSquare(row, col)) {
                    aiBoard[row][col] = " ";
                    continue;
                }

                aiBoard[row][col] = mapPiece(square.piece());
            }
        }

        return aiBoard;
    }

    private static boolean isPlayableSquare(int row, int col) {
        return (row + col) % 2 != 0;
    }

    private static String mapPiece(Piece piece) {
        if (piece == null) return " ";

        String color = piece.color().toString();

        if (piece.isKing()) {
            return color + "K";
        }

        return color;
    }
}