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
    private final RabbitMQTopology rabbitMQTopology;
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
        log.info("Published Checkers game result message: {}", message);
    }
}
