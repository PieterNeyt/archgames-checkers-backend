package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Square(int row, int col, SquareColor color, Piece piece) {

    public Square(int row, int col, SquareColor color) {
        this(row, col, color, null);
    }

    public boolean isEmpty() {
        return piece == null;
    }

    public Square withPiece(Piece newPiece) {
        return new Square(this.row, this.col, this.color, newPiece);
    }

    public Square emptied() {
        return new Square(this.row, this.col, this.color, null);
    }
}