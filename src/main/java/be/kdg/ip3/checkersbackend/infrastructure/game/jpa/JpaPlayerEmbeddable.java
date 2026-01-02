package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class JpaPlayerEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerType type;

    @Column(name = "profile_id")
    private UUID profileId;
    @Column(name = "session_id")
    private UUID sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceColor color;

    @Column(nullable = false)
    private String displayName;

    public JpaPlayerEmbeddable() {}

    public JpaPlayerEmbeddable(PlayerType type, UUID profileId,
                               PieceColor color, String displayName, UUID sessionId) {
        this.type = type;
        this.profileId = profileId;
        this.color = color;
        this.displayName = displayName;
        this.sessionId = sessionId;
    }

    public static JpaPlayerEmbeddable fromDomain(Player player) {
        if (player == null) {
            return null;
        }

        return new JpaPlayerEmbeddable(
                player.type(),
                player.profileId(),
                player.color(),
                player.displayName(),
                player.sessionId()
        );
    }


    public Player toDomain() {
        return new Player(
                type,
                profileId,
                sessionId,
                color,
                displayName
        );
    }
}