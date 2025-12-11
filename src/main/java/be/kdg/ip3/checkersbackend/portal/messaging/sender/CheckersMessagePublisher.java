package be.kdg.ip3.checkersbackend.portal.messaging.sender;

import be.kdg.ip3.checkersbackend.portal.messaging.config.CheckersGameResultMessage;
import be.kdg.ip3.checkersbackend.portal.messaging.config.RabbitMQTopology;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckersMessagePublisher {
    private final RabbitTemplate rabbitTemplate;
    public CheckersMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishGameResult(CheckersGameResultMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.CHECKERS_EXCHANGE_NAME,
                "checkers.game.result",
                message
        );
        log.info("Published Checkers game result message: {}", message);
    }
}
