package be.kdg.ip3.checkersbackend.infrastructure.game;

import be.kdg.ip3.checkersbackend.domain.game.GameRepository;
import be.kdg.ip3.checkersbackend.infrastructure.game.jpa.JpaGameRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DbGameRepository implements GameRepository {

    private final JpaGameRepository jpaGameRepository;

    public DbGameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }


}
