package be.kdg.ip3.checkersbackend.api.dto.portal;

import java.util.UUID;

public record SessionInfo(
        UUID sessionId,
        UUID gameLobbyId,
        UUID playerId,
        UUID gameId,
        String gamerTag
) {
}
