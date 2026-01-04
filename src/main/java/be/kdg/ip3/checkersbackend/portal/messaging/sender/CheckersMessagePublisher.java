package be.kdg.ip3.checkersbackend.portal.messaging.sender;

import be.kdg.ip3.checkersbackend.portal.messaging.config.AchievementUnlockedMessage;
import be.kdg.ip3.checkersbackend.portal.messaging.config.CheckersGameResultMessage;
import be.kdg.ip3.checkersbackend.portal.messaging.config.RabbitMQTopology;
import be.kdg.ip3.checkersbackend.portal.messaging.config.RegisterGameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
        var message = new RegisterGameMessage(
                title,
                description,
                imageUrl,
                gameUrl,
                BigDecimal.valueOf(price),
                genre,
                maxLobbySize
        );
        rabbitTemplate.convertAndSend(
                rabbitMQTopology.REGISTER_GAME_EXCHANGE,
                "register.game.checkers",
                message
        );
        log.info("Published register game message: {}", message);
    }
}
