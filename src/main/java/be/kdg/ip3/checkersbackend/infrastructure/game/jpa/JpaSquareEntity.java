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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "piece_id")
    private JpaPieceEntity piece;

    public JpaSquareEntity() {}

    public JpaSquareEntity(int row, int col, SquareColor color, JpaPieceEntity piece) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.piece = piece;
    }

    public static JpaSquareEntity fromDomain(Square square) {
        JpaPieceEntity pieceEntity = null;
        if (!square.isEmpty()) {
            pieceEntity = JpaPieceEntity.fromDomain(square.getPiece());
        }

        return new JpaSquareEntity(
                square.getRow(),
                square.getCol(),
                square.getColor(),
                pieceEntity
        );
    }

    public Square toDomain() {
        Square square = new Square(row, col, color);

        if (piece != null) {
            square.placePiece(piece.toDomain());
        }

        return square;
    }
}