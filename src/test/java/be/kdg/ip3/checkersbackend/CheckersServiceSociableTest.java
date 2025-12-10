package be.kdg.ip3.checkersbackend;

import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckersServiceSociableTest {

    @Mock
    GameRepository gameRepository;

    CheckersService service;

    @BeforeEach
    void setUp() {
        service = new CheckersService(gameRepository);
    }

    @Nested
    class StartGame {

        @Test
        void startGameVsAi_createsGameAndSaves() {
            var game = service.startGameVsAi();

            //:TODO voorlopig gwn zo testen. als later speler random kleur krijgt moet test ofc worden aagepast
            assertThat(game).isNotNull();
            assertThat(game.getPlayerWhite().type()).isEqualTo(PlayerType.HUMAN);
            assertThat(game.getPlayerBlack().type()).isEqualTo(PlayerType.AI);

            verify(gameRepository).save(game);
        }

        @Test
        void startGameVsPlayer_createsGameAndSaves() {
            var game = service.startGameVsPlayer();

            //:TODO voorlopig gwn zo testen. als later speler random kleur krijgt moet test ofc worden aagepast
            assertThat(game).isNotNull();
            assertThat(game.getPlayerWhite().color()).isNotEqualTo(game.getPlayerBlack().color());

            verify(gameRepository).save(game);
        }
    }

    @Nested
    class GetGame {

        @Test
        void getGame_existingGame_returnsGame() {
            var gameId = new GameId(UUID.randomUUID());
            var game = new Game(
                    Player.createHumanPlayer(UUID.randomUUID(), PieceColor.WHITE, "P1"),
                    Player.createAiPlayer(PieceColor.BLACK)
            );

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            var result = service.getGame(gameId);

            assertThat(result).isEqualTo(game);
            verify(gameRepository).findById(gameId);
        }

        @Test
        void getGame_nonExistingGame_throws() {
            var gameId = new GameId(UUID.randomUUID());

            when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getGame(gameId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(gameId.id().toString());
        }
    }

    @Nested
    class MakeMove {

        @Test
        void makeMove_callsGameAndSaves() {
            var gameId = new GameId(UUID.randomUUID());
            var game = spy(new Game(
                    Player.createHumanPlayer(UUID.randomUUID(), PieceColor.WHITE, "P1"),
                    Player.createAiPlayer(PieceColor.BLACK)
            ));

            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            // geldige zet
            var fromRow = 5;
            var fromCol = 0;

            var toRow = 4;
            var toCol = 1;

            var result = service.makeMove(gameId, fromRow, fromCol, toRow, toCol);

            verify(game).makeMove(fromRow, fromCol, toRow, toCol);
            verify(gameRepository).save(game);

            assertThat(result).isEqualTo(game);
        }
    }

    @Nested
    class GetValidMoves {

        @Test
        void getValidMoves_returnsMoves() {
            var gameId = new GameId(UUID.randomUUID());
            var game = mock(Game.class);
            when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

            var row = 2;
            var col = 1;
            List<Move> moves = List.of();
            when(game.getValidMovesForPiece(row, col)).thenReturn(moves);

            var result = service.getValidMoves(gameId, row, col);

            assertThat(result).isEqualTo(moves);
            verify(game).getValidMovesForPiece(row, col);
        }
    }
}
