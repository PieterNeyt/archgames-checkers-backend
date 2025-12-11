package be.kdg.ip3.checkersbackend.portal.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String CHECKERS_EXCHANGE_NAME = "checkers-exchange";
    public static final String CHECKERS_QUEUE_NAME = "checkers-queue";


    // Order topology

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
}