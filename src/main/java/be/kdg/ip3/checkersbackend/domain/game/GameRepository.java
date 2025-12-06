package be.kdg.ip3.checkersbackend.domain.game;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface GameRepository {
    void save(Game game);
    Optional<Game> findById(GameId gameId);
}
