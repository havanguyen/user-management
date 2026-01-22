package com.hanguyen.registercourses.messaging;
import com.hanguyen.registercourses.config.RabbitMQConfig;
import com.hanguyen.registercourses.entity.Enrollment;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import com.hanguyen.registercourses.service.registration.RegistrationCommandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RegistrationMessageConsumer {
    RegistrationCommandService registrationCommandService;
    EnrollmentRepository enrollmentRepository;
    @RabbitListener(queues = RabbitMQConfig.REGISTRATION_QUEUE)
    public void processRegistration(RegistrationMessage message) {
        log.info("Processing registration message: requestId={}, type={}",
                message.getRequestId(), message.getType());
        try {
            switch (message.getType()) {
                case REGISTER -> handleRegister(message);
                case DROP -> handleDrop(message);
            }
            log.info("Registration processed successfully: requestId={}", message.getRequestId());
        } catch (AppException e) {
            log.warn("Registration failed: requestId={}, error={}",
                    message.getRequestId(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing registration: requestId={}", message.getRequestId(), e);
            throw e; 
        }
    }
    private void handleRegister(RegistrationMessage message) {
        Enrollment enrollment = registrationCommandService.registerCourse(
                message.getStudentId(),
                message.getCourseId());
        log.info("Student {} registered for course {}, enrollment status: {}",
                message.getStudentId(), message.getCourseId(), enrollment.getStatus());
    }
    private void handleDrop(RegistrationMessage message) {
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(message.getStudentId(), message.getCourseId())
                .orElseThrow(() -> {
                    log.warn("Enrollment not found for drop request: studentId={}, courseId={}",
                            message.getStudentId(), message.getCourseId());
                    return new RuntimeException("Enrollment not found");
                });
        registrationCommandService.dropCourse(message.getStudentId(), enrollment.getId());
        log.info("Student {} dropped course {}", message.getStudentId(), message.getCourseId());
    }
}
