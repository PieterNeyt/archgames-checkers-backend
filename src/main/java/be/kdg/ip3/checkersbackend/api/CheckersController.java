package be.kdg.ip3.checkersbackend.api;

import be.kdg.ip3.checkersbackend.api.dto.game.GameDto;
import be.kdg.ip3.checkersbackend.api.dto.game.MakeMoveRequest;
import be.kdg.ip3.checkersbackend.api.dto.game.MoveDto;
import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.game.AiDifficulty;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkers")
public class CheckersController {

    private final CheckersService checkersService;

    public CheckersController(CheckersService checkersService) {
        this.checkersService = checkersService;

    }
    @PostMapping("/{sessionId}/start-singleplayer")
    public ResponseEntity<GameDto> startSingle(
            @PathVariable UUID sessionId,
            @RequestParam UUID lobbyId,
            @RequestParam AiDifficulty difficulty
    ) {
        var game = checkersService.startSinglePlayer(sessionId, lobbyId, difficulty);
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @PostMapping("/{sessionId}/start-multiplayer")
    public ResponseEntity<GameDto> startMulti(
            @PathVariable UUID sessionId,
            @RequestParam UUID lobbyId
    ) {
        var game = checkersService.joinOrCreateMultiplayer(sessionId, lobbyId);
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID gameId) {
        var game = checkersService.getGame(new GameId(gameId));
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @GetMapping("/{gameId}/valid-moves/{row}/{col}")
    public ResponseEntity<List<MoveDto>> getValidMoves(
            @PathVariable UUID gameId,
            @PathVariable int row,
            @PathVariable int col) {
        var moves = checkersService.getValidMoves(new GameId(gameId), row, col);
        var moveDtos = moves.stream()
                .map(MoveDto::fromDomain)
                .toList();
        return ResponseEntity.ok(moveDtos);
    }

    @PostMapping("/{sessionId}/{gameId}/move")
    public ResponseEntity<GameDto> makeMove(
            @PathVariable UUID sessionId,
            @PathVariable UUID gameId,
            @RequestBody MakeMoveRequest request) {

        var game = checkersService.makeMove(
                new GameId(gameId),
                sessionId,
                request.fromRow(),
                request.fromCol(),
                request.toRow(),
                request.toCol()
        );
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @PostMapping("/{gameId}/move/ai")
    public ResponseEntity<GameDto> makeMove(
            @PathVariable UUID gameId) {

        var game = checkersService.makeMove(
                new GameId(gameId)
        );
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }
}