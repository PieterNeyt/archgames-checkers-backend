package be.kdg.ip3.checkersbackend.domain.game;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.player.Position;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

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
    private final List<Move> moves;

    public Game(Player playerWhite, Player playerBlack) {
        this(GameId.create(), playerWhite, playerBlack, new Board(), GameState.IN_PROGRESS, PieceColor.WHITE, new ArrayList<>());
    }

    public Game(GameId gameId, Player playerWhite, Player playerBlack, Board board,
                GameState state, PieceColor currentPlayerColor, List<Move> moves) {
        if (playerWhite.color() == playerBlack.color()) {
            throw new IllegalArgumentException("Players must have different colors");
        }
        this.gameId = gameId;
        this.board = board;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
        this.moves = new ArrayList<>(moves);
    }


    public List<Position> getPlayablePieces() {
        return board.getPiecesWithValidMoves(currentPlayerColor);
    }

    public List<Move> getValidMovesForPiece(int row, int col) {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        var pieceMoves = board.getValidMoves(row, col, currentPlayerColor);

        var mandatoryJumpExists = board.hasJumpsAvailable(currentPlayerColor);

        if (mandatoryJumpExists) {
            return pieceMoves.stream()
                    .filter(Move::isJump)
                    .toList();
        }

        return pieceMoves;
    }

    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        var validMoves = getValidMovesForPiece(fromRow, fromCol);

        var requestedMove = validMoves.stream()
                .filter(m -> m.getToRow() == toRow && m.getToCol() == toCol)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid move or capture mandatory"));


        var executedMove = board.executeMove(requestedMove, getCurrentPlayerColor());

        this.moves.add(executedMove);


        if (executedMove.isJump()) {
            var additionalJumps = board.getValidJumps(toRow, toCol, board.getSquare(toRow, toCol).getPiece(), currentPlayerColor);

            if (!additionalJumps.isEmpty()) {
                return; // zodat speler opnieuw kan spelen
            }
        }

        switchTurn();
        checkGameOver();
    }

    private void switchTurn() {
        if (currentPlayerColor == PieceColor.WHITE) {
            currentPlayerColor = PieceColor.BLACK;
        } else {
            currentPlayerColor = PieceColor.WHITE;
        }
    }


    private void checkGameOver() {

        PieceColor otherPlayerColor = (currentPlayerColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        boolean currentPlayerHasMoves = board.hasMovesAvailable(currentPlayerColor);
        boolean otherPlayerHasMoves = board.hasMovesAvailable(otherPlayerColor);

        if (!currentPlayerHasMoves && !otherPlayerHasMoves) {
            state = GameState.DRAW;
        } else if (!currentPlayerHasMoves) {
            state = (otherPlayerColor == PieceColor.WHITE) ? GameState.WHITE_WON : GameState.BLACK_WON;
        }
    }


}