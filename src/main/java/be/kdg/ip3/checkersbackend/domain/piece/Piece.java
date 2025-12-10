package be.kdg.ip3.checkersbackend.domain.piece;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Piece(PieceId pieceId, PieceColor color, PieceType type) {

    public Piece(PieceColor color, PieceType type) {
        this(PieceId.create(), color, type);
    }

    public Piece promoted() {
        if (this.type == PieceType.KING) {
            return this;
        }
        return new Piece(this.pieceId, this.color, PieceType.KING);
    }

    public boolean isKing() {
        return this.type == PieceType.KING;
    }
}