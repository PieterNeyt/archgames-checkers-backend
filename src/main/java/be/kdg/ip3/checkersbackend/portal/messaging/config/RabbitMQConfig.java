package be.kdg.ip3.checkersbackend.portal.messaging.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessagingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessagingMessageConverter messagingMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messagingMessageConverter);
        return factory;
    }

    @Bean
    MessagingMessageConverter messagingMessageConverter() {
        return new MessagingMessageConverter();
    }
}
