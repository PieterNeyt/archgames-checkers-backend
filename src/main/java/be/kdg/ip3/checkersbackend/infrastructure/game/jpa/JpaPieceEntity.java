package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.piece.PieceId;
import be.kdg.ip3.checkersbackend.domain.piece.PieceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "piece", schema = "checkers")
public class JpaPieceEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceColor color;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceType type;

    public JpaPieceEntity() {}

    public JpaPieceEntity(UUID id, PieceColor color, PieceType type) {
        this.id = id;
        this.color = color;
        this.type = type;
    }

    public static JpaPieceEntity fromDomain(Piece piece) {
        return new JpaPieceEntity(
                piece.getPieceId().id(),
                piece.getColor(),
                piece.getType()
        );
    }

    public Piece toDomain() {
        return new Piece(
                new PieceId(id),
                color,
                type
        );
    }
}
