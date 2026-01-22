package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.constant.EnrollmentStatus;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.CourseRepository;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import com.hanguyen.registercourses.repository.RegistrationPeriodRepository;
import com.hanguyen.registercourses.repository.StudentRepository;
import com.hanguyen.registercourses.service.domain.TimeConflictChecker;
import com.hanguyen.registercourses.service.domain.TuitionCalculationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RegistrationCommandService {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int MAX_CREDITS_PER_SEMESTER = 24;

    CourseRepository courseRepository;
    RegistrationPeriodRepository registrationPeriodRepository;
    StudentRepository studentRepository;
    EnrollmentRepository enrollmentRepository;
    TimeConflictChecker timeConflictChecker;
    TuitionCalculationService tuitionCalculationService;

    /**
     * Đăng ký khóa học với xử lý Race Condition và kiểm tra trùng lịch
     */
    @Transactional
    public Enrollment registerCourse(String studentId, String courseId) {
        return registerCourseWithRetry(studentId, courseId, 0);
    }

    /**
     * Đăng ký khóa học với retry logic cho Optimistic Locking
     */
    private Enrollment registerCourseWithRetry(String studentId, String courseId, int attempt) {
        try {
            return doRegisterCourse(studentId, courseId);
        } catch (OptimisticLockingFailureException e) {
            if (attempt < MAX_RETRY_ATTEMPTS) {
                log.warn("Optimistic lock conflict on course {}, retry attempt {}", courseId, attempt + 1);
                return registerCourseWithRetry(studentId, courseId, attempt + 1);
            }
            log.error("Failed to register course {} after {} attempts", courseId, MAX_RETRY_ATTEMPTS);
            throw new AppException(ErrorCode.COURSE_REGISTRATION_CONFLICT);
        }
    }

    private Enrollment doRegisterCourse(String studentId, String courseId) {
        // 1. Kiểm tra thời gian đăng ký
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));

        // 2. Load student và course
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // 3. Kiểm tra course thuộc kỳ hiện tại
        if (!course.getSemester().getId().equals(activePeriod.getSemester().getId())) {
            throw new AppException(ErrorCode.COURSE_NOT_IN_ACTIVE_PERIOD);
        }

        // 4. Kiểm tra đã đăng ký môn này chưa
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        // 5. Kiểm tra đã đăng ký môn học (subject) này chưa
        boolean alreadyEnrolledSubject = enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                studentId, course.getSemester().getId(), course.getSubject().getId());
        if (alreadyEnrolledSubject) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        // 6. Kiểm tra prerequisites
        checkPrerequisites(student, course.getSubject());

        // 7. Kiểm tra trùng lịch học
        if (timeConflictChecker.hasTimeConflict(studentId, course)) {
            throw new AppException(ErrorCode.TIME_CONFLICT);
        }

        // 8. Kiểm tra giới hạn tín chỉ
        int newCredits = course.getSubject().getCredits();
        if (tuitionCalculationService.exceedsCreditLimit(studentId, course.getSemester().getId(), newCredits,
                MAX_CREDITS_PER_SEMESTER)) {
            throw new AppException(ErrorCode.CREDIT_LIMIT_EXCEEDED);
        }

        // 9. Kiểm tra lớp còn chỗ không
        if (!course.hasAvailableSlot()) {
            // Nếu lớp đầy, thêm vào waitlist
            return addToWaitlist(student, course);
        }

        // 10. Đăng ký thành công - sử dụng domain method
        course.incrementStudentCount();
        courseRepository.save(course); // @Version sẽ xử lý race condition

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .registrationTime(LocalDateTime.now())
                .status(EnrollmentStatus.REGISTERED)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    /**
     * Thêm vào danh sách chờ
     */
    private Enrollment addToWaitlist(Student student, Course course) {
        int queueOrder = enrollmentRepository.countWaitlistByCourseId(course.getId()) + 1;

        Enrollment waitlistEntry = Enrollment.builder()
                .student(student)
                .course(course)
                .registrationTime(LocalDateTime.now())
                .status(EnrollmentStatus.WAITLIST)
                .queueOrder(queueOrder)
                .build();

        log.info("Student {} added to waitlist for course {}, position {}",
                student.getId(), course.getId(), queueOrder);

        return enrollmentRepository.save(waitlistEntry);
    }

    /**
     * Hủy đăng ký và xử lý waitlist
     */
    @Transactional
    public void dropCourse(String studentId, String enrollmentId) {
        registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_DROP_COURSE);
        }

        Course course = enrollment.getCourse();
        EnrollmentStatus previousStatus = enrollment.getStatus();

        // Xóa enrollment
        enrollmentRepository.delete(enrollment);

        // Nếu người hủy đang REGISTERED, giảm sĩ số và promote người đầu waitlist
        if (previousStatus == EnrollmentStatus.REGISTERED) {
            course.decrementStudentCount();
            courseRepository.save(course);

            // Promote người đầu waitlist
            promoteFromWaitlist(course);
        }
    }

    /**
     * Đưa người đầu tiên trong waitlist lên REGISTERED
     */
    private void promoteFromWaitlist(Course course) {
        List<Enrollment> waitlist = enrollmentRepository.findWaitlistByCourseIdOrderByQueueOrder(course.getId());

        if (!waitlist.isEmpty()) {
            Enrollment firstInQueue = waitlist.get(0);
            firstInQueue.setStatus(EnrollmentStatus.REGISTERED);
            firstInQueue.setQueueOrder(null);
            enrollmentRepository.save(firstInQueue);

            course.incrementStudentCount();
            courseRepository.save(course);

            log.info("Student {} promoted from waitlist to registered for course {}",
                    firstInQueue.getStudent().getId(), course.getId());

            // Cập nhật queue order cho những người còn lại
            for (int i = 1; i < waitlist.size(); i++) {
                Enrollment entry = waitlist.get(i);
                entry.setQueueOrder(i);
                enrollmentRepository.save(entry);
            }
        }
    }

    private void checkPrerequisites(Student student, Subject subject) {
        Set<Subject> prerequisites = subject.getPrerequisites();
        if (prerequisites == null || prerequisites.isEmpty()) {
            return;
        }

        Set<String> passedSubjectIds = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .filter(e -> e.getGrade() != null && e.getGrade() >= 4.0)
                .map(e -> e.getCourse().getSubject().getId())
                .collect(Collectors.toSet());

        for (Subject prereq : prerequisites) {
            if (!passedSubjectIds.contains(prereq.getId())) {
                throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
            }
        }
    }
}
