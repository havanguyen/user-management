package com.hanguyen.registercourses.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    public static final String REGISTRATION_QUEUE = "registration.queue";
    public static final String REGISTRATION_EXCHANGE = "registration.exchange";
    public static final String REGISTRATION_ROUTING_KEY = "registration.routing.key";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.routing.key";
    @Bean
    public Queue registrationQueue() {
        return QueueBuilder.durable(REGISTRATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", REGISTRATION_QUEUE + ".dlq")
                .build();
    }
    @Bean
    public DirectExchange registrationExchange() {
        return new DirectExchange(REGISTRATION_EXCHANGE);
    }
    @Bean
    public Binding registrationBinding(Queue registrationQueue, DirectExchange registrationExchange) {
        return BindingBuilder.bind(registrationQueue)
                .to(registrationExchange)
                .with(REGISTRATION_ROUTING_KEY);
    }
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
