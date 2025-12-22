package be.kdg.ip3.checkersbackend.application;

import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveRequest;
import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveResponse;
import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.domain.SessionId;
import be.kdg.ip3.checkersbackend.domain.game.*;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
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

    public Game startGameVsAi(UUID sessionId, AiDifficulty difficulty) {
        var session = validateSession(sessionId);

        //:TODO nog zorgen dat naam van speler wordt meegegeven maar dat is voor multiplayer us
        var humanPlayer = Player.createHumanPlayer(session.playerId(), getRandomColor(), "Player");
        var aiPlayer = Player.createAiPlayer(humanPlayer.color().opposite());

        var game = createGame(humanPlayer, aiPlayer, difficulty);

        return game;
    }

    private SessionInfo validateSession(UUID sessionId) {
        return launcherClient.validateSession(new SessionId(sessionId));
    }

    public Game startGameVsPlayer(UUID sessionId) {

        var session = validateSession(sessionId);

        //:TODO Tijdelijk nog een random UUID voor speler, later vervangen door echte gebruiker
        var player1 = Player.createHumanPlayer(UUID.randomUUID(), getRandomColor(), "Player 1");
        var player2 = Player.createHumanPlayer(UUID.randomUUID(), player1.color().opposite(), "Player 2");

        return createGame(player1, player2, null);
    }

    private Game createGame(Player player1, Player player2, AiDifficulty difficulty) {
        var playerWhite = (player1.color() == PieceColor.WHITE) ? player1 : player2;
        var playerBlack = (player1.color() == PieceColor.BLACK) ? player1 : player2;

        var game = difficulty != null
                ? new Game(playerWhite, playerBlack, difficulty)
                : new Game(playerWhite, playerBlack);

        gameRepository.save(game);
        return game;
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

        var session = validateSession(sessionId);

        var game = getGame(gameId);
        var currentPlayer = game.getCurrentPlayer();

        if (!currentPlayer.profileId().equals(session.playerId())) {
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