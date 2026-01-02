package be.kdg.ip3.checkersbackend.application;

import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveRequest;
import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveResponse;
import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.domain.SessionId;
import be.kdg.ip3.checkersbackend.domain.game.*;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import be.kdg.ip3.checkersbackend.portal.ai.AiClient;
import be.kdg.ip3.checkersbackend.portal.ai.AiMoveParser;
import be.kdg.ip3.checkersbackend.portal.rest.LauncherClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class CheckersService {

    private final GameRepository gameRepository;
    private final AiClient aiClient;
    private final LauncherClient launcherClient;

    public CheckersService(GameRepository gameRepository, AiClient aiClient, LauncherClient launcherClient) {
        this.gameRepository = gameRepository;
        this.aiClient = aiClient;
        this.launcherClient = launcherClient;
    }

    public Game startSinglePlayer(UUID sessionId, UUID lobbyId, AiDifficulty difficulty) {
        var session = validateSession(sessionId);

        // Check of er al een actieve game is voor deze lobby
        gameRepository.findActiveGameByLobbyId(lobbyId).ifPresent(g -> {
            if (g.getAiDifficulty() != null) {
                var activePlayer = g.getPlayerWhite().type() == PlayerType.HUMAN
                        ? g.getPlayerWhite() : g.getPlayerBlack();
                throw new IllegalStateException(
                        "Wacht tot speler " + activePlayer.displayName() + " klaar is met hun Singleplayer spel."
                );
            } else {
                throw new IllegalStateException(
                        "Er is een multiplayer spel actief in deze lobby."
                );
            }
        });

        var humanPlayer = Player.createHumanPlayer(
                session.playerId(),
                sessionId,
                getRandomColor(),
                session.gamerTag()
        );

        var aiPlayer = Player.createAiPlayer(humanPlayer.color().opposite());

        Player whitePlayer;
        Player blackPlayer;

        if (humanPlayer.color() == PieceColor.WHITE) {
            whitePlayer = humanPlayer;
            blackPlayer = aiPlayer;
        } else {
            whitePlayer = aiPlayer;
            blackPlayer = humanPlayer;
        }

        var game = new Game(whitePlayer, blackPlayer, difficulty, lobbyId);

        gameRepository.save(game);
        return game;
    }

    public Game joinOrCreateMultiplayer(UUID sessionId, UUID lobbyId) {
        var session = validateSession(sessionId);
        var activeGameOpt = gameRepository.findActiveGameByLobbyId(lobbyId);

        if (activeGameOpt.isPresent()) {
            var game = activeGameOpt.get();
            if (game.getAiDifficulty() != null) {
                var activePlayer = game.getPlayerWhite().type() == PlayerType.HUMAN
                        ? game.getPlayerWhite() : game.getPlayerBlack();
                throw new IllegalStateException(
                        "Wacht tot speler " + activePlayer.displayName() +
                                " klaar is met hun Singleplayer spel."
                );
            }


            if (game.getState() == GameState.WAITING_FOR_OPPONENT) {
                if (game.getWaitingPlayer().sessionId().equals(sessionId)) {
                    return game;
                }

                game.addPlayerTwo(Player.createHumanPlayer(
                        session.playerId(),
                        sessionId,
                        game.getWaitingPlayer().color().opposite(),
                        session.gamerTag()
                ));
                game.startGame();
                gameRepository.save(game);
                return game;
            } else {

                if (game.getPlayerWhite().sessionId().equals(sessionId) ||
                        game.getPlayerBlack().sessionId().equals(sessionId)) {
                    return game;
                }
                throw new IllegalStateException("Dit spel is al vol.");
            }
        }

        var player1 = Player.createHumanPlayer(
                session.playerId(),
                sessionId,
                getRandomColor(),
                session.gamerTag()
        );
        var game = Game.createWaitingMultiplayer(player1, lobbyId);
        gameRepository.save(game);
        return game;
    }
    private SessionInfo validateSession(UUID sessionId) {
        return launcherClient.validateSession(new SessionId(sessionId));
    }

    private PieceColor getRandomColor() {
        return new Random().nextBoolean() ? PieceColor.WHITE : PieceColor.BLACK;
    }

    public Game getGame(GameId gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    public List<Move> getValidMoves(GameId gameId, int row, int col) {
        var game = getGame(gameId);
        return game.getValidMovesForPiece(row, col);
    }

    public Game makeMove(GameId gameId, UUID sessionId, int fromRow, int fromCol, int toRow, int toCol) {

        var game = getGame(gameId);
        var currentPlayer = game.getCurrentPlayer();

        if (!currentPlayer.sessionId().equals(sessionId)) {
            throw new IllegalArgumentException("It's not your turn");
        }

        game.makeMove(fromRow, fromCol, toRow, toCol);

        if (game.getAiDifficulty() != null) {
            notifyAiIfGameFinished(game);
        }
        gameRepository.save(game);
        return game;
    }

    public Game makeMove(GameId gameId) {
        var game = getGame(gameId);
        AiMoveResponse response = aiClient.requestAiMove(AiMoveRequest.fromDomain(game));

        if (response.executed_moves() != null && !response.executed_moves().isEmpty()) {

            String[] parts = response.executed_moves().split("-");

            for (int i = 0; i < parts.length - 1; i++) {

                var from = AiMoveParser.getBackendPosition(parts[i]);
                var to = AiMoveParser.getBackendPosition(parts[i + 1]);

                game.makeMove(from.row(), from.col(), to.row(), to.col());
            }
        }

        notifyAiIfGameFinished(game);

        gameRepository.save(game);
        return game;
    }

    private void notifyAiIfGameFinished(Game game) {
        if (game.getState() != GameState.IN_PROGRESS) {
            aiClient.requestAiMove(AiMoveRequest.fromDomain(game));
        }
    }


}