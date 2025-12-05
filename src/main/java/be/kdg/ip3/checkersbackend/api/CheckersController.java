package be.kdg.ip3.checkersbackend.api;

import be.kdg.ip3.checkersbackend.application.CheckersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkers")
public class CheckersController {

    private final CheckersService checkersService;

    public CheckersController(CheckersService checkersService) {
        this.checkersService = checkersService;
    }

//    @GetMapping("/{gameId}")
//    public ResponseEntity<GameDto> getGameById(@PathVariable String gameId) {
//        //Game game = checkersService.findById(new GameId(UUID.fromString(gameId)));
//        return ResponseEntity.ok(GameDto.FromDomain(game));
//    }
}
