package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Position;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "move", schema = "checkers")
public class JpaMoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private int fromRow;

    @Column(nullable = false)
    private int fromCol;

    @Column(nullable = false)
    private int toRow;

    @Column(nullable = false)
    private int toCol;

    @Column(nullable = false)
    private boolean isJump;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ElementCollection
    @CollectionTable(
            name = "move_captured_positions",
            schema = "checkers",
            joinColumns = @JoinColumn(name = "move_id")
    )
    private List<JpaPositionEmbeddable> capturedPositions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "player_color", nullable = false)
    private PieceColor playerColor;

    public JpaMoveEntity() {}

    public JpaMoveEntity(int fromRow, int fromCol, int toRow, int toCol,
                         boolean isJump, LocalDateTime timestamp,
                         List<JpaPositionEmbeddable> capturedPositions,
                         PieceColor playerColor) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.isJump = isJump;
        this.timestamp = timestamp;
        this.capturedPositions = capturedPositions;
        this.playerColor = playerColor;
    }

    public static JpaMoveEntity fromDomain(Move move) {
        List<JpaPositionEmbeddable> positions = move.getCapturedPositions().stream()
                .map(JpaPositionEmbeddable::fromDomain)
                .toList();

        return new JpaMoveEntity(
                move.getFromRow(),
                move.getFromCol(),
                move.getToRow(),
                move.getToCol(),
                move.isJump(),
                move.getTimestamp(),
                positions,
                move.getPlayerColor()
        );
    }

    public Move toDomain() {
        List<Position> positions = capturedPositions.stream()
                .map(JpaPositionEmbeddable::toDomain)
                .toList();

        return new Move(fromRow, fromCol, toRow, toCol, positions, playerColor);
    }
}