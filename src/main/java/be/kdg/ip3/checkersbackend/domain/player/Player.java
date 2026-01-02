package be.kdg.ip3.checkersbackend.domain.player;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;


@ValueObject
public record Player(
        PlayerType type,
        UUID profileId,
        UUID sessionId,
        PieceColor color,
        String displayName
) {

    public static Player createHumanPlayer(UUID profileId, UUID sessionId, PieceColor color, String displayName) {
        if (profileId == null || sessionId == null) {
            throw new IllegalArgumentException("ProfileId and SessionId cannot be null for human player");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        return new Player(PlayerType.HUMAN, profileId, sessionId, color, displayName);
    }

    public static Player createAiPlayer(PieceColor color) {
        String aiDisplayName = "AI Player (" + color + ")";
        return new Player(PlayerType.AI, null, null, color, aiDisplayName);
    }
}