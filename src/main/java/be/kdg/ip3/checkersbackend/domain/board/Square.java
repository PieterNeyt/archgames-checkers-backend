package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;

@Getter
@ValueObject
public class Square {
    private final int row;
    private final int col;
    private final SquareColor color;
    private Piece piece;



    public Square(int row, int col, SquareColor color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    public boolean isEmpty() {
        return piece == null;
    }
    public void placePiece(Piece piece) {
        this.piece = piece;
    }

}
