package be.kdg.ip3.checkersbackend.domain.piece;

import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Getter
@Entity
public class Piece {
    @Identity
    private final PieceId pieceId;
    private final PieceColor color;
    private PieceType type;

    public Piece(PieceColor color, PieceType type) {
        this(PieceId.create(), color, type);
    }

    public Piece(PieceId id, PieceColor color, PieceType type) {
        this.pieceId = id;
        this.color = color;
        this.type = type;
    }

}
