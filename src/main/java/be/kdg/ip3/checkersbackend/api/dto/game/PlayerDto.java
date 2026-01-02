package be.kdg.ip3.checkersbackend.api.dto.game;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;

import java.util.UUID;

public record PlayerDto(
        PlayerType type,
        UUID profileId,
        PieceColor color,
        String displayName,
        UUID sessionId
        ) {
    public static PlayerDto fromDomain(Player player) {
        if (player == null) {
            return null;
        }

        return new PlayerDto(
                player.type(),
                player.profileId(),
                player.color(),
                player.displayName(),
                player.sessionId()
        );
    }

}