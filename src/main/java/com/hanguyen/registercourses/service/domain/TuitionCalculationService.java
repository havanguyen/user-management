package com.hanguyen.registercourses.service.domain;
import com.hanguyen.registercourses.entity.Enrollment;
import com.hanguyen.registercourses.entity.Major;
import com.hanguyen.registercourses.entity.Student;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TuitionCalculationService {
    EnrollmentRepository enrollmentRepository;
    public BigDecimal calculateTuition(Student student, String semesterId) {
        Major major = student.getMajor();
        if (major == null || major.getPricePerCredit() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal pricePerCredit = major.getPricePerCredit();
        int totalCredits = calculateTotalCredits(student.getId(), semesterId);
        return pricePerCredit.multiply(BigDecimal.valueOf(totalCredits));
    }
    public int calculateTotalCredits(String studentId, String semesterId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndSemesterId(studentId, semesterId);
        return enrollments.stream()
                .filter(e -> e.getStatus() == com.hanguyen.registercourses.constant.EnrollmentStatus.REGISTERED)
                .mapToInt(e -> e.getCourse().getSubject().getCredits())
                .sum();
    }
    public boolean exceedsCreditLimit(String studentId, String semesterId, int newCredits, int maxCredits) {
        int currentCredits = calculateTotalCredits(studentId, semesterId);
        return (currentCredits + newCredits) > maxCredits;
    }
}
