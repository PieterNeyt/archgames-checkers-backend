package be.kdg.ip3.checkersbackend.portal.ai;

import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;

public final class AiBoardMapper {

    private AiBoardMapper() {}

    public static String[][] toAiBoard(Board board) {
        String[][] aiBoard = new String[8][8];
        Square[][] squares = board.getSquares();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = squares[7 - row][7 - col];

                Piece piece = square.piece();

                if (piece == null) {
                    aiBoard[row][col] = " ";
                } else {
                    aiBoard[row][col] = mapPiece(piece);
                }
            }
        }
        return aiBoard;
    }

    private static String mapPiece(Piece piece) {

        String colorChar = piece.color().toString().substring(0, 1).toUpperCase();

        if (piece.isKing()) {
            return colorChar + "K";
        }
        return colorChar;
    }
}