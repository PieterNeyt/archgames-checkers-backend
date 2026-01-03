package be.kdg.ip3.checkersbackend;

import be.kdg.ip3.checkersbackend.api.dto.external.AiMoveResponse;
import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.game.*;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
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

    @Mock
    private GameRepository gameRepository;
    @Mock
    private AiClient aiClient;
    @Mock
    private LauncherClient launcherClient;
    @Mock
    private CheckersMessagePublisher checkersMessagePublisher;

    @BeforeEach
    void setUp() {
     //   service = new CheckersService(gameRepository, aiClient,launcherClient,checkersMessagePublisher);
    }
/*
    @Nested
    class StartGame {

        @Test
        void startGameVsAi_shouldSaveGame() {
            //arrange
            var playerId = UUID.randomUUID();

            when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            playerId,
                            UUID.randomUUID()
                    ));

            //act
            var game = service.startGameVsAi(playerId, AiDifficulty.MEDIUM);

            //Assert
            assertThat(game).isNotNull();
            verify(gameRepository).save(any(Game.class));
        }


        @Test
        void startGameVsAi_AiIsWhite_AiMovesImmediately() {
            //arrange
            var playerId = UUID.randomUUID();

            var aiPlayer = Player.createAiPlayer(PieceColor.WHITE);
            var humanPlayer = Player.createHumanPlayer(playerId, PieceColor.BLACK, "Human");
            var game = new Game(aiPlayer, humanPlayer, AiDifficulty.EASY);

            when(gameRepository.findById(any())).thenReturn(Optional.of(game));
            when(aiClient.requestAiMove(any())).thenReturn(new AiMoveResponse("test", "IN_PROGRESS", "9-13"));

            //act
            service.makeMove(game.getGameId());

            //assert
            assertThat(game.getCurrentPlayerColor()).isEqualTo(PieceColor.BLACK);
            assertThat(game.getMoves()).hasSize(1);
        }
    }

    @Nested
    class MakeMove {
        @Test
        void makeMove_validMove_updatesBoard() {
            //arrange
            var playerId = UUID.randomUUID();

            when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            playerId,
                            UUID.randomUUID()
                    ));

            var gameId = new GameId(UUID.randomUUID());
            var game = new Game(Player.createHumanPlayer(playerId, PieceColor.WHITE, "P1"), Player.createAiPlayer(PieceColor.BLACK));

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            //act
            service.makeMove(gameId, playerId, 5, 2, 4, 3);

            //assert
            assertThat(game.getBoard().getSquare(5, 2).isEmpty()).isTrue();
            assertThat(game.getBoard().getSquare(4, 3).piece()).isNotNull();
            assertThat(game.getCurrentPlayerColor()).isEqualTo(PieceColor.BLACK);
            verify(gameRepository).save(game);
        }

        @Test
        void makeMove_wrongPlayer_throwsException() {
            //arrange
            var playerId = UUID.randomUUID();


            var wrongPlayerId = UUID.randomUUID();
            var gameId = new GameId(UUID.randomUUID());
            var game = new Game(Player.createHumanPlayer(playerId, PieceColor.WHITE, "P1"), Player.createAiPlayer(PieceColor.BLACK));

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
            when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            wrongPlayerId,
                            UUID.randomUUID()
                    ));
            //act & assert
            assertThatThrownBy(() -> service.makeMove(gameId, wrongPlayerId, 5, 2, 4, 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not your turn");
        }
    }


    @Nested
    class MandatoryJump {
        @Test
        void makeMove_failsWhenJumpIsAvailable() {
            //arrange
            var playerId = UUID.randomUUID();

            when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            playerId,
                            UUID.randomUUID()
                    ));

            var gameId = new GameId(UUID.randomUUID());
            var pWhite = Player.createHumanPlayer(playerId, PieceColor.WHITE, "H");
            var pBlack = Player.createHumanPlayer(UUID.randomUUID(), PieceColor.BLACK, "H2");
            var game = new Game(pWhite, pBlack);

            game.makeMove(5, 2, 4, 3); // Wit
            game.makeMove(2, 5, 3, 4); // Zwart


            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            //act & assert
            assertThatThrownBy(() -> service.makeMove(gameId, playerId, 5, 0, 4, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("capture mandatory");
        }

        @Test
        void getValidMoves_onlyReturnsJumps() {
            //arrange
            var gameId = new GameId(UUID.randomUUID());
            var pWhite = Player.createHumanPlayer(UUID.randomUUID(), PieceColor.WHITE, "H");
            var pBlack = Player.createHumanPlayer(UUID.randomUUID(), PieceColor.BLACK, "H2");
            var game = new Game(pWhite, pBlack);

            game.makeMove(5, 2, 4, 3);
            game.makeMove(2, 5, 3, 4);

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            //act
            var moves = service.getValidMoves(gameId, 4, 3);

            //assert
            assertThat(moves).allMatch(Move::isJump);
            assertThat(moves).hasSize(1);
        }
    }

    @Nested
    class GameOver {
        @Test
        void notifyAi_whenGameIsFinished() {
            //arrange
            var gameId = new GameId(UUID.randomUUID());
            var pWhite = Player.createHumanPlayer(UUID.randomUUID(), PieceColor.WHITE, "H");
            var pBlack = Player.createAiPlayer(PieceColor.BLACK);

            var game = spy(new Game(pWhite, pBlack, AiDifficulty.MEDIUM));
            when(game.getState()).thenReturn(GameState.WHITE_WON);

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            when(launcherClient.validateSession(any()))
                    .thenReturn(new SessionInfo(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            pWhite.profileId(),
                            UUID.randomUUID()
                    ));

            //act
            service.makeMove(gameId, pWhite.profileId(), 5, 2, 4, 3);

            //assert
            verify(aiClient, atLeastOnce()).requestAiMove(any());
        }
    }*/
}