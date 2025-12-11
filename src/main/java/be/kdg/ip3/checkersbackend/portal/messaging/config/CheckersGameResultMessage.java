package be.kdg.ip3.checkersbackend.portal.messaging.config;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckersGameResultMessage(UUID sessionId, String winner, LocalDateTime timestamp) {
}
