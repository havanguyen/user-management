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

/**
 * Producer for sending registration messages to RabbitMQ
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RegistrationMessageProducer {

    RabbitTemplate rabbitTemplate;

    /**
     * Gửi yêu cầu đăng ký vào hàng đợi để xử lý bất đồng bộ
     * 
     * @return requestId để track trạng thái đăng ký
     */
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

    /**
     * Gửi yêu cầu hủy đăng ký vào hàng đợi
     */
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
