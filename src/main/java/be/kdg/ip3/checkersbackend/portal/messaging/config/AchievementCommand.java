package be.kdg.ip3.checkersbackend.portal.messaging.config;

import java.util.UUID;

public record AchievementCommand(
        UUID id,
        String externalAchId,
        String title,
        String description,
        String imageUrl
) {
}