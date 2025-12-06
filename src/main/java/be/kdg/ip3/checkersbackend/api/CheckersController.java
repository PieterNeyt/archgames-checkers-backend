package be.kdg.ip3.checkersbackend.api;

import be.kdg.ip3.checkersbackend.api.dto.GameDto;
import be.kdg.ip3.checkersbackend.application.CheckersService;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/checkers")
public class CheckersController {

    private final CheckersService checkersService;

    public CheckersController(CheckersService checkersService) {
        this.checkersService = checkersService;
    }

    @PostMapping("/start-ai")
    public ResponseEntity<GameDto> startGameVsAi() {
        var game = checkersService.startGameVsAi();
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @PostMapping("/start-player")
    public ResponseEntity<GameDto> startGameVsPlayer() {
        var game = checkersService.startGameVsPlayer();
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID gameId) {
        var game = checkersService.getGame(new GameId(gameId));
        return ResponseEntity.ok(GameDto.fromDomain(game));
    }

}
