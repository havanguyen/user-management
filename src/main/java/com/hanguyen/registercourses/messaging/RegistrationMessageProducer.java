package com.hanguyen.registercourses.messaging;
import com.hanguyen.registercourses.config.RabbitMQConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RegistrationMessageProducer {
    RabbitTemplate rabbitTemplate;
    public String sendRegistrationRequest(String studentId, String courseId) {
        String requestId = UUID.randomUUID().toString();
        RegistrationMessage message = RegistrationMessage.builder()
                .studentId(studentId)
                .courseId(courseId)
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .type(RegistrationMessage.RegistrationType.REGISTER)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REGISTRATION_EXCHANGE,
                RabbitMQConfig.REGISTRATION_ROUTING_KEY,
                message);
        log.info("Registration request sent: requestId={}, studentId={}, courseId={}",
                requestId, studentId, courseId);
        return requestId;
    }
    public String sendDropRequest(String studentId, String courseId) {
        String requestId = UUID.randomUUID().toString();
        RegistrationMessage message = RegistrationMessage.builder()
                .studentId(studentId)
                .courseId(courseId)
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .type(RegistrationMessage.RegistrationType.DROP)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REGISTRATION_EXCHANGE,
                RabbitMQConfig.REGISTRATION_ROUTING_KEY,
                message);
        log.info("Drop request sent: requestId={}, studentId={}, courseId={}",
                requestId, studentId, courseId);
        return requestId;
    }
}
