package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.board.SquareColor;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "square", schema = "checkers")
public class JpaSquareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @Setter
    private JpaBoardEntity board;

    @Column(nullable = false)
    private int row;

    @Column(nullable = false)
    private int col;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SquareColor color;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "piece_id")
    @Setter
    private JpaPieceEntity piece;

    public JpaSquareEntity() {}

    public JpaSquareEntity(int row, int col, SquareColor color, JpaPieceEntity piece) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.piece = piece;
    }

    public static JpaSquareEntity fromDomain(Square square) {
        var pieceEntity = square.isEmpty() ? null : JpaPieceEntity.fromDomain(square.piece());

        return new JpaSquareEntity(
                square.row(),
                square.col(),
                square.color(),
                pieceEntity
        );
    }

    public Square toDomain() {
        var domainPiece = (piece != null) ? piece.toDomain() : null;

        return new Square(row, col, color, domainPiece);
    }
}