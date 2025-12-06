package be.kdg.ip3.checkersbackend.domain.player;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@Getter
@ValueObject
public class Player {
    private final PlayerType type;
    private final UUID profileId;
    private final PieceColor color;
    private final String displayName;


    // Constructor voor echte speler
    public Player(UUID profileId, PieceColor color, String displayName) {
        this.type = PlayerType.HUMAN;
        this.profileId = profileId;
        this.color = color;
        this.displayName = displayName;
    }

    // Constructor voor AI
    public Player(PieceColor color) {
        this.type = PlayerType.AI;
        this.profileId = null;
        this.color = color;
        this.displayName = "AI Player (" + color + ")";
    }

    public static Player createHumanPlayer(UUID profileId, PieceColor color, String displayName) {
        if (profileId == null) {
            throw new IllegalArgumentException("ProfileId cannot be null for human player");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty");
        }
        return new Player(profileId, color, displayName);
    }

    public static Player createAiPlayer(PieceColor color) {
        return new Player(color);
    }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    public boolean isAi() {
        return type == PlayerType.AI;
    }

    public boolean hasColor(PieceColor color) {
        return this.color == color;
    }
}