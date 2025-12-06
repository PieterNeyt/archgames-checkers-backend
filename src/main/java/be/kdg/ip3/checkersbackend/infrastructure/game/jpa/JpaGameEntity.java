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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private JpaBoardEntity board;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "player_white_type")),
            @AttributeOverride(name = "profileId", column = @Column(name = "player_white_profile_id")),
            @AttributeOverride(name = "color", column = @Column(name = "player_white_color")),
            @AttributeOverride(name = "displayName", column = @Column(name = "player_white_display_name"))
    })
    private JpaPlayerEmbeddable playerWhite;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "player_black_type")),
            @AttributeOverride(name = "profileId", column = @Column(name = "player_black_profile_id")),
            @AttributeOverride(name = "color", column = @Column(name = "player_black_color")),
            @AttributeOverride(name = "displayName", column = @Column(name = "player_black_display_name"))
    })
    private JpaPlayerEmbeddable playerBlack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceColor currentPlayerColor;

    public JpaGameEntity() {}

    public JpaGameEntity(UUID id, JpaBoardEntity board, JpaPlayerEmbeddable playerWhite,
                         JpaPlayerEmbeddable playerBlack, GameState state, PieceColor currentPlayerColor) {
        this.id = id;
        this.board = board;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
    }

    public static JpaGameEntity fromDomain(Game game) {
        JpaBoardEntity boardEntity = JpaBoardEntity.fromDomain(game.getBoard());

        return new JpaGameEntity(
                game.getGameId().id(),
                boardEntity,
                JpaPlayerEmbeddable.fromDomain(game.getPlayerWhite()),
                JpaPlayerEmbeddable.fromDomain(game.getPlayerBlack()),
                game.getState(),
                game.getCurrentPlayerColor()
        );
    }

    public Game toDomain() {
        return new Game(
                new GameId(id),
                playerWhite.toDomain(),
                playerBlack.toDomain(),
                board.toDomain(),
                state,
                currentPlayerColor
        );
    }

}