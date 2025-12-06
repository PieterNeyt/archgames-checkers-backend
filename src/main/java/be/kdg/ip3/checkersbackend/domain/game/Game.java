package be.kdg.ip3.checkersbackend.domain.game;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.board.Board;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@Getter
@AggregateRoot
public class Game {
    @Identity
    private final GameId gameId;
    private final Board board;
    private final Player playerWhite;
    private final Player playerBlack;
    private GameState state;
    private PieceColor currentPlayerColor;

    public Game(Player playerWhite, Player playerBlack) {
        this(GameId.create(), playerWhite, playerBlack, new Board(), GameState.IN_PROGRESS, PieceColor.WHITE);
    }

    public Game(GameId gameId, Player playerWhite, Player playerBlack, Board board, GameState state, PieceColor currentPlayerColor) {
        if (playerWhite.getColor() == playerBlack.getColor()) {
            throw new IllegalArgumentException("Players must have different colors");
        }
        this.gameId = gameId;
        this.board = board;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
    }

}