package be.kdg.ip3.checkersbackend.infrastructure.game;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameId;
import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaBoardEntity;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaGameEntity;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaGameRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbGameRepository implements GameRepository {

    private final JpaGameRepository jpaGameRepository;

    public DbGameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        jpaGameRepository.save(JpaGameEntity.fromDomain(game));
    }

    @Override
    public Optional<Game> findById(GameId gameId) {
        return jpaGameRepository.findById(gameId.id())
                .map(JpaGameEntity::toDomain);
    }
}