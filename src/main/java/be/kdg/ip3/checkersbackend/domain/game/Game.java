package be.kdg.ip3.checkersbackend.domain.game;

import be.kdg.ip3.checkersbackend.domain.Move;
import be.kdg.ip3.checkersbackend.domain.Player;
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
    private final Player player1;
    private final Player player2;
    private GameState state;

    public Game(Player player1, Player player2) {
        this(GameId.create(), player1, player2);
    }

    public Game(GameId gameId, Player player1, Player player2) {
        this.gameId = gameId;
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.state = GameState.IN_PROGRESS;
    }

    public boolean makeMove(Move move) {

        return true;
    }


}
