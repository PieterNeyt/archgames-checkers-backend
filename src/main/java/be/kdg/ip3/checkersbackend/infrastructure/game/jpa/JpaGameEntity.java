package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "game", schema = "checkers")
public class JpaGameEntity {

    @Id
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "game_id")
    private List<JpaMoveEntity> moves = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameState state;

    @Enumerated(EnumType.STRING)
    private PieceColor currentPlayerColor;



    public JpaGameEntity() {}

    public JpaGameEntity(UUID id, JpaBoardEntity board, JpaPlayerEmbeddable playerWhite,
                         JpaPlayerEmbeddable playerBlack, List<JpaMoveEntity> moves,
                         GameState state, PieceColor currentPlayerColor) {
        this.id = id;
        this.board = board;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.moves = moves;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
    }

    public void updateFromDomain(Game domainGame) {
        this.state = domainGame.getState();
        this.currentPlayerColor = domainGame.getCurrentPlayerColor();

        this.board.updateFromDomain(domainGame.getBoard());
        this.moves.clear();
        domainGame.getMoves().forEach(move -> this.moves.add(JpaMoveEntity.fromDomain(move)));
    }

    public static JpaGameEntity fromDomain(Game game) {

        var jpaMoves = game.getMoves().stream()
                .map(JpaMoveEntity::fromDomain).toList();

        return new JpaGameEntity(
                game.getGameId().id(),
                JpaBoardEntity.fromDomain(game.getBoard()),
                JpaPlayerEmbeddable.fromDomain(game.getPlayerWhite()),
                JpaPlayerEmbeddable.fromDomain(game.getPlayerBlack()),
                jpaMoves,
                game.getState(),
                game.getCurrentPlayerColor()
        );
    }

    public Game toDomain() {
        var domainMoves = moves.stream()
                .map(JpaMoveEntity::toDomain)
                .collect(Collectors.toList());

        var white = playerWhite.toDomain();
        var black = playerBlack.toDomain();

        return new Game(
                new GameId(id),
                white,
                black,
                board.toDomain(),
                state,
                currentPlayerColor,
                domainMoves
        );
    }
}