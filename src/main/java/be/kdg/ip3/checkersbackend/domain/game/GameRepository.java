package be.kdg.ip3.checkersbackend.domain.game;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository {
    void save(Game game);
    Optional<Game> findById(GameId gameId);
    Optional<Game> findActiveGameByLobbyId(UUID lobbyId);
}
