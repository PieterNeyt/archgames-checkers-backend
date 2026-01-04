package be.kdg.ip3.checkersbackend.portal.messaging.config;

import java.math.BigDecimal;

public record RegisterGameMessage(
        String title,
        String description,
        String imageUrl,
        String gameUrl,
        BigDecimal price,
        String genre,
        int maxlobbysize
) {
}