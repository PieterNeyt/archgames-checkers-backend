package be.kdg.ip3.checkersbackend.portal.messaging.sender;

import be.kdg.ip3.checkersbackend.portal.messaging.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CheckersMessagePublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQTopology rabbitMQTopology;

    @Value("${game.title}")
    private String title;
    @Value("${game.description}")
    private String description;
    @Value("${game.imageUrl}")
    private String imageUrl;
    @Value("${game.gameUrl}")
    private String gameUrl;
    @Value("${game.price}")
    private double price;
    @Value("${game.genre}")
    private String genre;
    @Value("${game.maxlobbysize}")
    private int maxLobbySize;

    public CheckersMessagePublisher(RabbitTemplate rabbitTemplate, RabbitMQTopology rabbitMQTopology) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQTopology = rabbitMQTopology;
    }

    public void publishGameResult(CheckersGameResultMessage message) {
        rabbitTemplate.convertAndSend(
                rabbitMQTopology.CHECKERS_EXCHANGE_NAME,
                "checkers.game.result",
                message
        );
    }

    public void publishAchievementUnlock(AchievementUnlockedMessage message) {

        rabbitTemplate.convertAndSend(
                rabbitMQTopology.ACHIEVEMENT_EXCHANGE_NAME,
                "checkers.achievement.unlock",
                message
        );

    }
    @EventListener(ApplicationReadyEvent.class)
    public void publishGameRegister() {
        var achievements = List.of(
                new AchievementCommand(
                        UUID.randomUUID(),
                        "won_a_game_vs_ai",
                        "AI Verslagen",
                        "Je hebt een wedstrijd gewonnen tegen de AI. Netjes gespeeld!",
                        "https://www.budgettrophy.com/media/catalog/product/S/L/SL1914_02.png"
                ),
                new AchievementCommand(
                        UUID.randomUUID(),
                        "lost_a_game_vs_ai",
                        "AI Was Te Sterk",
                        "Je verloor een wedstrijd tegen de AI. Volgende keer beter!",
                        "https://www.budgettrophy.com/media/catalog/product/S/L/SL1914_02.png"
                ),
                new AchievementCommand(
                        UUID.randomUUID(),
                        "lost_a_game",
                        "Nederlaag",
                        "Je hebt een wedstrijd verloren tegen een andere speler.",
                        "https://www.budgettrophy.com/media/catalog/product/S/L/SL1914_02.png"
                ),
                new AchievementCommand(
                        UUID.randomUUID(),
                        "won_a_game",
                        "Overwinning",
                        "Gefeliciteerd! Je hebt een wedstrijd gewonnen.",
                        "https://www.budgettrophy.com/media/catalog/product/S/L/SL1914_02.png"
                ),
                new AchievementCommand(
                        UUID.randomUUID(),
                        "drew_a_game",
                        "Gelijkspel",
                        "De wedstrijd eindigde in een gelijkspel. Spannend tot het einde!",
                        "https://www.budgettrophy.com/media/catalog/product/S/L/SL1914_02.png"
                )
        );

        var message = new RegisterGameMessage(
                title,
                description,
                imageUrl,
                gameUrl,
                BigDecimal.valueOf(price),
                genre,
                maxLobbySize,
                achievements
        );

        rabbitTemplate.convertAndSend(
                rabbitMQTopology.REGISTER_GAME_EXCHANGE,
                "register.game.checkers",
                message
        );
        log.info("Published register game message: {}", message);
    }
}
