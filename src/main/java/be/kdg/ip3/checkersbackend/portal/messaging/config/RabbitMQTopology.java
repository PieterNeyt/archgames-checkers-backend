package be.kdg.ip3.checkersbackend.portal.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    @Value("${checkers.exchange.name}")
    public String CHECKERS_EXCHANGE_NAME ;
    @Value("${checkers.queue.name}")
    public String CHECKERS_QUEUE_NAME ;
    @Value("${achievement.exchange.name}")
    public String ACHIEVEMENT_EXCHANGE_NAME;
    @Value("${register.exchange.name}")
    public String REGISTER_GAME_EXCHANGE;

    @Bean
    TopicExchange checkersExchange() {
        return new TopicExchange(CHECKERS_EXCHANGE_NAME);
    }

    @Bean
    Queue checkersQueue() {
        return QueueBuilder.nonDurable(CHECKERS_QUEUE_NAME).build();
    }

    @Bean
    Binding checkersQueueToCheckersExchangeBinding() {
        return BindingBuilder.bind(checkersQueue()).to(checkersExchange()).with("checkers.game.*");
    }

    @Bean
    TopicExchange achievementExchange() {
        return new TopicExchange(ACHIEVEMENT_EXCHANGE_NAME);
    }
    @Bean
    Queue achievementQueue() {
        return QueueBuilder.nonDurable("achievement-queue").build();
    }

    @Bean
    Binding achievementQueueToAchievementExchangeBinding() {
        return BindingBuilder.bind(achievementQueue())
                .to(achievementExchange())
                .with("*.achievement.unlock");
    }

}