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
import be.kdg.ip3.checkersbackend.portal.messaging.config.AchievementUnlockedMessage;
import be.kdg.ip3.checkersbackend.portal.messaging.config.CheckersGameResultMessage;
import be.kdg.ip3.checkersbackend.portal.messaging.sender.CheckersMessagePublisher;
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
    private final CheckersMessagePublisher checkersMessagePublisher;

    public CheckersService(GameRepository gameRepository, AiClient aiClient, LauncherClient launcherClient, CheckersMessagePublisher checkersMessagePublisher) {
        this.gameRepository = gameRepository;
        this.aiClient = aiClient;
        this.launcherClient = launcherClient;
        this.checkersMessagePublisher = checkersMessagePublisher;
    }

    public Game startSinglePlayer(UUID sessionId, UUID lobbyId, AiDifficulty difficulty) {
        var session = validateSession(sessionId);

        gameRepository.findActiveGameByLobbyId(lobbyId)
                .ifPresent(Game::assertNoNewGameAllowed);

        var humanPlayer = Player.createHumanPlayer(
                session.playerId(),
                sessionId,
                getRandomColor(),
                session.gamerTag()
        );

        var aiPlayer = Player.createAiPlayer(humanPlayer.color().opposite());

        var game = Game.createSinglePlayer(
                humanPlayer,
                aiPlayer,
                difficulty,
                lobbyId,
                session.gameId()
        );

        gameRepository.save(game);
        return game;
    }


    public Game joinOrCreateMultiplayer(UUID sessionId, UUID lobbyId) {
        var session = validateSession(sessionId);
        var activeGameOpt = gameRepository.findActiveGameByLobbyId(lobbyId);

        if (activeGameOpt.isEmpty()) {
            return createNewMultiplayerGame(session, lobbyId);
        }

        var game = activeGameOpt.get();

        if (game.isPlayerInGame(sessionId)) {
            return game;
        }

        if (game.isSinglePlayer()) {
            throw new IllegalStateException(
                    "Wait until " + game.getHumanPlayer().displayName() + " is done with their game!"
            );
        }

        if (game.canPlayerJoin(sessionId)) {
            var newPlayer = Player.createHumanPlayer(
                    session.playerId(),
                    sessionId,
                    game.getWaitingPlayer().color().opposite(),
                    session.gamerTag()
            );
            game.joinAsSecondPlayer(newPlayer);
            gameRepository.save(game);
            return game;
        }

        throw new IllegalStateException("This game is full");
    }

    private Game createNewMultiplayerGame(SessionInfo session, UUID lobbyId) {
        var player = Player.createHumanPlayer(
                session.playerId(),
                session.sessionId(),
                getRandomColor(),
                session.gamerTag()
        );
        var game = Game.createWaitingMultiplayer(player, lobbyId, session.gameId());
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

        handleGameFinished(game);
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

        handleGameFinished(game);

        gameRepository.save(game);
        return game;
    }
    private void handleGameFinished(Game game) {

        if (game.getState() == GameState.IN_PROGRESS) {
            return;
        }

        var winner = game.getWinner();
        var loser = game.getLoser();

        if (game.isDraw()) {
            publishAchievementForPlayer(game.getPlayerWhite(), "drew_a_game", game.getPlatformGameId());
            publishAchievementForPlayer(game.getPlayerBlack(), "drew_a_game", game.getPlatformGameId());
        } else {

            checkersMessagePublisher.publishGameResult(
                    new CheckersGameResultMessage(
                            winner.sessionId(),
                            winner.displayName(),
                            java.time.LocalDateTime.now()
                    )
            );

            publishAchievementForPlayer(winner, "won_a_game", game.getPlatformGameId());
            publishAchievementForPlayer(loser, "lost_a_game", game.getPlatformGameId());
        }

        if (game.getAiPlayer() != null) {
            notifyAiIfGameFinished(game);
            publishAchievementForPlayer(winner, "won_a_game_vs_ai", game.getPlatformGameId());
            publishAchievementForPlayer(loser, "lost_a_game_vs_ai", game.getPlatformGameId());
        }
    }

    private void publishAchievementForPlayer(Player player, String achievementId, UUID gameUuid) {
        if (player != null && player.type() == PlayerType.HUMAN) {
            checkersMessagePublisher.publishAchievementUnlock(
                    new AchievementUnlockedMessage(achievementId, player.profileId(), gameUuid)
            );
        }
    }

    private void notifyAiIfGameFinished(Game game) {
        aiClient.requestAiMove(AiMoveRequest.fromDomain(game));
    }


}