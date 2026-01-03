package be.kdg.ip3.checkersbackend.portal.messaging.config;

import java.util.UUID;

public record AchievementUnlockedMessage(
        String externalAchId,
        UUID playerId,
        UUID gameId
) {
}