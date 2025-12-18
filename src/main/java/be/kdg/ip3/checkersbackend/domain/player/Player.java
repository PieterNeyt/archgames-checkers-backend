package be.kdg.ip3.checkersbackend.domain.player;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;


@ValueObject
public record Player(PlayerType type, UUID profileId, PieceColor color, String displayName) {

    public static Player createHumanPlayer(UUID profileId, PieceColor color, String displayName) {
        if (profileId == null) {
            throw new IllegalArgumentException("ProfileId cannot be null for human player");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }

        return new Player(PlayerType.HUMAN, profileId, color, displayName);
    }

    public static Player createAiPlayer(PieceColor color) {
        String aiDisplayName = "AI Player (" + color + ")";
        return new Player(PlayerType.AI, null, color, aiDisplayName);
    }
}