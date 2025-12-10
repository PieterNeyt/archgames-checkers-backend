package be.kdg.ip3.checkersbackend.infrastructure.game;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaGameEntity;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaGameRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class DbGameRepository implements GameRepository {

    private final JpaGameRepository jpaGameRepository;

    public DbGameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        JpaGameEntity entity = jpaGameRepository
                .findById(game.getGameId().id())
                .map(existing -> {
                    existing.updateFromDomain(game); // al bestaande game via updatefromdomain bewerken
                    return existing;
                })
                .orElseGet(() -> JpaGameEntity.fromDomain(game)); // nieuwe game

        jpaGameRepository.save(entity);
    }

    @Override
    public Optional<Game> findById(GameId gameId) {
        return jpaGameRepository.findById(gameId.id())
                .map(JpaGameEntity::toDomain);
    }
}
