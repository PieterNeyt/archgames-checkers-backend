package be.kdg.ip3.checkersbackend.domain.game;

import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import be.kdg.ip3.checkersbackend.domain.player.Position;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AggregateRoot
public class Game {

    @Identity
    private final GameId gameId;

    private Board board;

    private Player playerWhite;
    private Player playerBlack;

    private final UUID lobbyId;
    private final UUID gameTypeId;

    private GameState state;
    private PieceColor currentPlayerColor;

    private final List<Move> moves;

    private final AiDifficulty aiDifficulty;

    // Singleplayer (tegen AI)
    public Game(Player playerWhite, Player playerBlack, AiDifficulty aiDifficulty, UUID lobbyId,UUID gameTypeId ) {
        this(
                GameId.create(),
                playerWhite,
                playerBlack,
                new Board(),
                GameState.IN_PROGRESS,
                PieceColor.WHITE,
                new ArrayList<>(),
                aiDifficulty,
                lobbyId,
                gameTypeId
        );
    }

    public Game(
            GameId gameId,
            Player playerWhite,
            Player playerBlack,
            Board board,
            GameState state,
            PieceColor currentPlayerColor,
            List<Move> moves,
            AiDifficulty aiDifficulty,
            UUID lobbyId,
            UUID gameTypeId
    ) {
        this.gameId = gameId;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.board = board;
        this.state = state;
        this.currentPlayerColor = currentPlayerColor;
        this.moves = moves;
        this.aiDifficulty = aiDifficulty;
        this.lobbyId = lobbyId;
        this.gameTypeId = gameTypeId;
    }

    public static Game createWaitingMultiplayer(Player playerOne, UUID lobbyId,UUID gameTypeId) {
       if (playerOne.color() == PieceColor.WHITE) {
           return new Game(
                   GameId.create(),
                   playerOne,
                   null,
                   new Board(),
                   GameState.WAITING_FOR_OPPONENT,
                   PieceColor.WHITE,
                   new ArrayList<>(),
                   null,
                   lobbyId,
                   gameTypeId

           );
       } else {
           return new Game(
                   GameId.create(),
                   null,
                   playerOne,
                   new Board(),
                   GameState.WAITING_FOR_OPPONENT,
                   PieceColor.WHITE,
                   new ArrayList<>(),
                   null,
                   lobbyId,
                   gameTypeId
           );
       }
    }
    public Player getWaitingPlayer() {
        if (state != GameState.WAITING_FOR_OPPONENT) {
            throw new IllegalStateException("There is no waiting player in this game");
        }

        if (playerWhite != null && playerBlack == null) {
            return playerWhite;
        }

        if (playerBlack != null && playerWhite == null) {
            return playerBlack;
        }

        throw new IllegalStateException("Invalid game state: none or both players present");
    }


    public void addPlayerTwo(Player playerTwo) {
        if (state != GameState.WAITING_FOR_OPPONENT) {
            throw new IllegalStateException("Can't add a player to an ongoing game");
        }
        if (playerTwo.color() == PieceColor.WHITE) {
            this.playerWhite = playerTwo;
        }else {
            this.playerBlack = playerTwo;
        }

    }

    public void startGame() {
        if (state != GameState.WAITING_FOR_OPPONENT) {
            throw new IllegalStateException("Game couldn't be started");
        }
        this.state = GameState.IN_PROGRESS;
        this.currentPlayerColor = PieceColor.WHITE;
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
                .filter(m -> m.toRow() == toRow && m.toCol() == toCol)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid move"));

        var result = board.executeMove(requestedMove);

        this.board = result.newBoard();
        this.moves.add(result.executedMove());

        if (result.executedMove().isJump()) {
            var pieceAtDest = this.board.getSquare(toRow, toCol).piece();
            var additionalJumps =
                    this.board.getValidJumps(toRow, toCol, pieceAtDest, currentPlayerColor);

            if (!additionalJumps.isEmpty()) {
                return;
            }
        }

        switchTurn();
        checkGameOver();
    }

    private void switchTurn() {
        currentPlayerColor =
                (currentPlayerColor == PieceColor.WHITE)
                        ? PieceColor.BLACK
                        : PieceColor.WHITE;
    }

    public Player getCurrentPlayer() {
        return (playerWhite.color() == currentPlayerColor)
                ? playerWhite
                : playerBlack;
    }

    public Player getAiPlayer() {
        if (playerWhite.type() == PlayerType.AI) {
            return playerWhite;
        } else if (playerBlack.type() == PlayerType.AI) {
            return playerBlack;
        }
        return null;
    }


    private void checkGameOver() {
        var otherPlayerColor =
                (currentPlayerColor == PieceColor.WHITE)
                        ? PieceColor.BLACK
                        : PieceColor.WHITE;

        var currentPlayerHasMoves = board.hasMovesAvailable(currentPlayerColor);
        var otherPlayerHasMoves = board.hasMovesAvailable(otherPlayerColor);

        if (!currentPlayerHasMoves && !otherPlayerHasMoves) {
            state = GameState.DRAW;
        } else if (!currentPlayerHasMoves) {
            state = (otherPlayerColor == PieceColor.WHITE)
                    ? GameState.WHITE_WON
                    : GameState.BLACK_WON;
        }
    }

    public Player getWinner() {
        return switch (state) {
            case WHITE_WON -> playerWhite;
            case BLACK_WON -> playerBlack;
            case DRAW -> null;
            case IN_PROGRESS, WAITING_FOR_OPPONENT ->
                    throw new IllegalStateException("Game hasn't finished");
            default -> throw new IllegalStateException("invalid game state: " + state);
        };
    }

    public Player getLoser() {
        return switch (state) {
            case WHITE_WON -> playerBlack;
            case BLACK_WON -> playerWhite;
            case DRAW -> null;
            case IN_PROGRESS, WAITING_FOR_OPPONENT ->
                    throw new IllegalStateException("Game hasn't finished");
            default -> throw new IllegalStateException("invalid game: " + state);
        };
    }

    public boolean isDraw() {
        return state == GameState.DRAW;
    }
}
