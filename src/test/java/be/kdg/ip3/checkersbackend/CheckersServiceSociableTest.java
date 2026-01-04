package be.kdg.ip3.checkersbackend;

import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveResponse;
import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.game.*;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import be.kdg.ip3.checkersbackend.portal.ai.AiClient;
import be.kdg.ip3.checkersbackend.portal.messaging.sender.CheckersMessagePublisher;
import be.kdg.ip3.checkersbackend.portal.rest.LauncherClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckersServiceSociableTest {

    private CheckersService service;

    @Mock private GameRepository gameRepository;
    @Mock private AiClient aiClient;
    @Mock private LauncherClient launcherClient;
    @Mock private CheckersMessagePublisher checkersMessagePublisher;

    @BeforeEach
    void setUp() {
        service = new CheckersService(gameRepository, aiClient, launcherClient, checkersMessagePublisher);
    }

    @Nested
    class StartGame {

        @Test
        void startGameVsAi_shouldSaveGame() {
            var playerId = UUID.randomUUID();
            var sessionId = UUID.randomUUID();
            var lobbyId = UUID.randomUUID();

            lenient().when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            sessionId,
                            lobbyId,
                            playerId,
                            UUID.randomUUID(),
                            "TestPlayer"
                    ));

            var game = service.startSinglePlayer(sessionId, lobbyId, AiDifficulty.MEDIUM);

            assertThat(game).isNotNull();
            verify(gameRepository).save(any(Game.class));
        }

        @Test
        void startGameVsAi_AiIsWhite_AiMovesImmediately() {
            var playerId = UUID.randomUUID();
            var sessionId = UUID.randomUUID();

            // human player
            var humanPlayer = Player.createHumanPlayer(playerId, sessionId, PieceColor.BLACK, "Human");

            Player aiPlayerMock = mock(Player.class);
            when(aiPlayerMock.type()).thenReturn(PlayerType.AI);
            when(aiPlayerMock.color()).thenReturn(PieceColor.WHITE);

            var game = Game.createSinglePlayer(
                    humanPlayer,
                    aiPlayerMock,
                    AiDifficulty.EASY,
                    UUID.randomUUID(),
                    UUID.randomUUID()
            );

            when(gameRepository.findById(any())).thenReturn(Optional.of(game));
            when(aiClient.requestAiMove(any()))
                    .thenReturn(new AiMoveResponse("test", "IN_PROGRESS", "9-13"));

            service.makeMove(game.getGameId());

            assertThat(game.getCurrentPlayerColor()).isEqualTo(PieceColor.BLACK);
            assertThat(game.getMoves()).hasSize(1);
        }
    }

    @Nested
    class MakeMove {


        @Test
        void makeMove_wrongPlayer_throwsException() {
            var playerId = UUID.randomUUID();
            var sessionId = UUID.randomUUID();
            var lobbyId = UUID.randomUUID();

            var wrongSessionId = UUID.randomUUID();

            lenient().when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(sessionId, lobbyId, playerId, UUID.randomUUID(), "P1"));

            when(gameRepository.findActiveGameByLobbyId(lobbyId))
                    .thenReturn(Optional.empty());

            var game = service.startSinglePlayer(sessionId, lobbyId, AiDifficulty.EASY);
            var gameId = game.getGameId();

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            assertThatThrownBy(() -> service.makeMove(gameId, wrongSessionId, 5, 2, 4, 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not your turn");
        }
    }

    @Nested
    class MandatoryJump {

        @Test
        void makeMove_failsWhenJumpIsAvailable() {
            var playerId = UUID.randomUUID();
            var sessionId = UUID.randomUUID();

            lenient().when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(sessionId, UUID.randomUUID(), playerId, UUID.randomUUID(), "TestPlayer"));

            var gameId = new GameId(UUID.randomUUID());
            var pWhite = Player.createHumanPlayer(playerId, sessionId, PieceColor.WHITE, "H");
            var pBlack = Player.createHumanPlayer(UUID.randomUUID(), UUID.randomUUID(), PieceColor.BLACK, "H2");
            var game = Game.createWaitingMultiplayer(pWhite, UUID.randomUUID(), UUID.randomUUID());
            game.joinAsSecondPlayer(pBlack);

            game.makeMove(5, 2, 4, 3); // Wit
            game.makeMove(2, 5, 3, 4); // Zwart

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            assertThatThrownBy(() -> service.makeMove(gameId, sessionId, 5, 0, 4, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid move");
        }

        @Test
        void getValidMoves_onlyReturnsJumps() {
            var gameId = new GameId(UUID.randomUUID());
            var pWhite = Player.createHumanPlayer(UUID.randomUUID(), UUID.randomUUID(), PieceColor.WHITE, "H");
            var pBlack = Player.createHumanPlayer(UUID.randomUUID(), UUID.randomUUID(), PieceColor.BLACK, "H2");
            var game = Game.createWaitingMultiplayer(pWhite, UUID.randomUUID(), UUID.randomUUID());
            game.joinAsSecondPlayer(pBlack);

            game.makeMove(5, 2, 4, 3);
            game.makeMove(2, 5, 3, 4);

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            var moves = service.getValidMoves(gameId, 4, 3);

            assertThat(moves).allMatch(Move::isJump);
            assertThat(moves).hasSize(1);
        }
    }

}
