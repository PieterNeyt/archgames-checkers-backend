package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;



import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceColor color;

    @Column(nullable = false)
    private String displayName;

    public JpaPlayerEmbeddable() {}

    public JpaPlayerEmbeddable(PlayerType type, UUID profileId, PieceColor color, String displayName) {
        this.type = type;
        this.profileId = profileId;
        this.color = color;
        this.displayName = displayName;
    }

    public static JpaPlayerEmbeddable fromDomain(Player player) {
        return new JpaPlayerEmbeddable(
                player.getType(),
                player.getProfileId(),
                player.getColor(),
                player.getDisplayName()
        );
    }

    public Player toDomain() {
        if (type == PlayerType.HUMAN) {
            return Player.createHumanPlayer(profileId, color, displayName);
        } else {
            return Player.createAiPlayer(color);
        }
    }
}