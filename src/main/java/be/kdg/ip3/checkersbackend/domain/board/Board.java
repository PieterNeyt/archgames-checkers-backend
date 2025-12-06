package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.piece.PieceType;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;


@Getter
@Entity
public class Board {
    @Identity
    private final BoardId boardId;
    private final Square[][] board;

    public Board() {
       this(BoardId.create(), new  Square[8][8]);
        initializeBoard();
    }

    public Board(BoardId boardId,  Square[][] board) {
        this.boardId = boardId;
        this.board = board;
    }

    private void initializeBoard() {

        for (int i = 0; i < board.length; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                board[i][ii] = new Square(i, ii, ((i + ii) % 2 == 0) ? SquareColor.LIGHT_BROWN : SquareColor.DARK_BROWN);
            }
        }
        setupPieces();
    }

    private void setupPieces() {

        //black pieces
        for (int i = 0; i < board.length / 2-1 ; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                if ((i + ii) % 2 != 0) {
                    board[i][ii].placePiece(new Piece(PieceColor.BLACK, PieceType.MAN));
                }

            }
        }

        // white pieces
        for (int i = board.length / 2+1; i < board.length; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                if ((i + ii) % 2 != 0) {
                    board[i][ii].placePiece(new Piece(PieceColor.WHITE, PieceType.MAN));
                }
            }
        }

    }

    public Square getSquare(int row, int col) {
        return board[row][col];
    }

}
