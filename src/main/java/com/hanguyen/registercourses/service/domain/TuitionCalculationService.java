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

/**
 * Domain Service for calculating tuition based on registered credits
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TuitionCalculationService {

    EnrollmentRepository enrollmentRepository;

    /**
     * Tính học phí của sinh viên trong một kỳ
     *
     * @param student    Sinh viên
     * @param semesterId ID kỳ học
     * @return Tổng học phí
     */
    public BigDecimal calculateTuition(Student student, String semesterId) {
        Major major = student.getMajor();
        if (major == null || major.getPricePerCredit() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal pricePerCredit = major.getPricePerCredit();
        int totalCredits = calculateTotalCredits(student.getId(), semesterId);

        return pricePerCredit.multiply(BigDecimal.valueOf(totalCredits));
    }

    /**
     * Tính tổng số tín chỉ đã đăng ký trong kỳ
     */
    public int calculateTotalCredits(String studentId, String semesterId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndSemesterId(studentId, semesterId);

        return enrollments.stream()
                .filter(e -> e.getStatus() == com.hanguyen.registercourses.constant.EnrollmentStatus.REGISTERED)
                .mapToInt(e -> e.getCourse().getSubject().getCredits())
                .sum();
    }

    /**
     * Kiểm tra sinh viên có vượt quá giới hạn tín chỉ không
     *
     * @param studentId  ID sinh viên
     * @param semesterId ID kỳ học
     * @param newCredits Số tín chỉ của môn mới đăng ký
     * @param maxCredits Giới hạn tín chỉ tối đa (default 24)
     * @return true nếu vượt quá giới hạn
     */
    public boolean exceedsCreditLimit(String studentId, String semesterId, int newCredits, int maxCredits) {
        int currentCredits = calculateTotalCredits(studentId, semesterId);
        return (currentCredits + newCredits) > maxCredits;
    }
}
