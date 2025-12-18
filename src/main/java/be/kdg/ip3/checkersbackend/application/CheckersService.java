package be.kdg.ip3.checkersbackend.application;

import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveRequest;
import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.portal.ai.AiClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CheckersService {

    private final GameRepository gameRepository;
    private final AiClient aiClient;

    public CheckersService(GameRepository gameRepository, AiClient aiClient) {
        this.gameRepository = gameRepository;
        this.aiClient = aiClient;
    }

    public Game startGameVsAi() {
        //:TODO Tijdelijk nog een random UUID voor speler, later vervangen door echte gebruiker
        var tempPlayerId = UUID.randomUUID();
        var humanPlayer = Player.createHumanPlayer(tempPlayerId, PieceColor.WHITE, "Player");
        var aiPlayer = Player.createAiPlayer(PieceColor.BLACK);

        var game = new Game(humanPlayer, aiPlayer);
        gameRepository.save(game);

        return game;
    }

    public Game startGameVsPlayer() {
        //:TODO Tijdelijk nog een random UUID voor speler, later vervangen door echte gebruiker
        var playerWhite = UUID.randomUUID();
        var playerBlack = UUID.randomUUID();
        var player1 = Player.createHumanPlayer(playerWhite, PieceColor.WHITE, "Player 1");
        var player2 = Player.createHumanPlayer(playerBlack, PieceColor.BLACK, "Player 2");

        var game = new Game(player1, player2);
        gameRepository.save(game);

        return game;
    }

    public Game getGame(GameId gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    public List<Move> getValidMoves(GameId gameId, int row, int col) {
        var game = getGame(gameId);
        return game.getValidMovesForPiece(row, col);
    }

    public Game makeMove(GameId gameId, int fromRow, int fromCol, int toRow, int toCol) {
        var game = getGame(gameId);
        game.makeMove(fromRow, fromCol, toRow, toCol);
        gameRepository.save(game);
        return game;
    }

    public Game makeMove(GameId gameId) {
        var game = getGame(gameId);
        var response = aiClient.requestAiMove(AiMoveRequest.fromDomain(game));

        gameRepository.save(game);
        return game;
    }
}