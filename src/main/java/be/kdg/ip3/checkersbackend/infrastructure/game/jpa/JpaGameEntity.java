package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "game", schema = "checkers")
public class JpaGameEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID boardId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "playerWhite_type")),
            @AttributeOverride(name = "profileId", column = @Column(name = "playerWhite_profile_id")),
            @AttributeOverride(name = "color", column = @Column(name = "playerWhite_color")),
            @AttributeOverride(name = "displayName", column = @Column(name = "playerWhite_display_name"))
    })
    private JpaPlayerEmbeddable playerWhite;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "playerBlack_type")),
            @AttributeOverride(name = "profileId", column = @Column(name = "playerBlack_profile_id")),
            @AttributeOverride(name = "color", column = @Column(name = "playerBlack_color")),
            @AttributeOverride(name = "displayName", column = @Column(name = "playerBlack_display_name"))
    })
    private JpaPlayerEmbeddable playerBlack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceColor currentPlayerColor;

    public JpaGameEntity() {}

    public JpaGameEntity(UUID id, UUID boardId, JpaPlayerEmbeddable playerWhite,
                         JpaPlayerEmbeddable playerBlack, GameState state, PieceColor currentPlayerColor) {
        this.id = id;
        this.boardId = boardId;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
    }

    public static JpaGameEntity fromDomain(Game game) {
        return new JpaGameEntity(
                game.getGameId().id(),
                game.getBoard().getBoardId().id(),
                JpaPlayerEmbeddable.fromDomain(game.getPlayerWhite()),
                JpaPlayerEmbeddable.fromDomain(game.getPlayerBlack()),
                game.getState(),
                game.getCurrentPlayerColor()
        );
    }

    public Game toDomain(JpaBoardEntity boardEntity) {
        return new Game(
                new GameId(id),
                playerWhite.toDomain(),
                playerBlack.toDomain(),
                boardEntity.toDomain(),
                state,
                currentPlayerColor
        );
    }
}