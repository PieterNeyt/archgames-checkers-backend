package be.kdg.ip3.checkersbackend.api;

import be.kdg.ip3.checkersbackend.api.dto.game.GameDto;
import be.kdg.ip3.checkersbackend.api.dto.game.MakeMoveRequest;
import be.kdg.ip3.checkersbackend.api.dto.game.MoveDto;
import be.kdg.ip3.checkersbackend.api.dto.portal.SessionInfo;
import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.SessionId;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.portal.rest.LauncherClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkers")
public class CheckersController {

    private final CheckersService checkersService;
    private final LauncherClient launcherClient;

    public CheckersController(CheckersService checkersService, LauncherClient launcherClient) {
        this.checkersService = checkersService;
        this.launcherClient = launcherClient;
    }

    @PostMapping("{sessionId}/start-ai")
    public ResponseEntity<GameDto> startGameVsAi(
            @PathVariable UUID sessionId
    ) {
        var game = checkersService.startGameVsAi();
        SessionInfo session = launcherClient.validateSession(new SessionId(sessionId));

        return ResponseEntity
                .created(URI.create("/api/checkers/" + game.getGameId().id()))
                .body(GameDto.fromDomain(game));
    }

    @PostMapping("{sessionId}/start-player")
    public ResponseEntity<GameDto> startGameVsPlayer(
            @PathVariable UUID sessionId
    ) {
        SessionInfo session = launcherClient.validateSession(new SessionId(sessionId));

        var game = checkersService.startGameVsPlayer();
        return ResponseEntity
                .created(URI.create("/api/checkers/" + game.getGameId().id()))
                .body(GameDto.fromDomain(game));
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

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameDto> makeMove(
            @PathVariable UUID gameId,
            @RequestBody MakeMoveRequest request) {

        var game = checkersService.makeMove(
                new GameId(gameId),
                request.fromRow(),
                request.fromCol(),
                request.toRow(),
                request.toCol()
        );
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

}