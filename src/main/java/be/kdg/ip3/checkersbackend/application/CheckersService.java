package be.kdg.ip3.checkersbackend.application;

import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CheckersService {

    private final GameRepository gameRepository;

    public CheckersService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


}
