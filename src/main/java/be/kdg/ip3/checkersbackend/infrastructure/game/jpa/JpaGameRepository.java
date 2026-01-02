package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaGameRepository extends JpaRepository<JpaGameEntity, UUID> {
    Optional<JpaGameEntity> findByLobbyIdAndStateNot(UUID lobbyId, GameState state);
    // Zoek een actieve game voor deze lobby die niet FINISHED is
    @Query("SELECT g FROM JpaGameEntity g WHERE g.lobbyId = :lobbyId AND g.state IN ('IN_PROGRESS', 'WAITING_FOR_OPPONENT')")
    Optional<JpaGameEntity> findActiveGameByLobbyId(@Param("lobbyId") UUID lobbyId);
}
