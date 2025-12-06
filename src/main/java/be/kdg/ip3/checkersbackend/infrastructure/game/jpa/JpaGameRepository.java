package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaGameRepository extends JpaRepository<JpaGameEntity, UUID> {

}
