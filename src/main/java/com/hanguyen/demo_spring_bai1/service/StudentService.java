package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.response.MyScheduleResponse;
import com.hanguyen.demo_spring_bai1.entity.*;
import com.hanguyen.demo_spring_bai1.enums.EnrollmentStatus;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.exception.registration.AlreadyEnrolledException;
import com.hanguyen.demo_spring_bai1.exception.registration.CourseFullException;
import com.hanguyen.demo_spring_bai1.exception.registration.PrerequisiteNotMetException;
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.EnrollmentRepository;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final CourseRepository courseRepository;
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Course> getOpenCoursesForRegistration() {
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new BusinessException("Registration is not open at the moment."));
        String semesterId = activePeriod.getSemester().getId();
        return courseRepository.findBySemesterId(semesterId);
    }

    public MyScheduleResponse getMySchedule(String studentId, String semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseSemesterId(studentId, semesterId);

        List<MyScheduleResponse.ScheduleItem> scheduleItems = enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    return MyScheduleResponse.ScheduleItem.builder()
                            .courseCode(course.getCourseCode())
                            .subjectName(course.getSubject().getName())
                            .credits(course.getSubject().getCredits())
                            .lecturerName(course.getLecturer().getUser().getFirstname() + " " + course.getLecturer().getUser().getLastname())
                            .scheduleInfo(course.getScheduleInfo())
                            .build();
                })
                .collect(Collectors.toList());

        return MyScheduleResponse.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getUser().getFirstname() + " " + student.getUser().getLastname())
                .semesterName(enrollments.isEmpty() ? "N/A" : enrollments.get(0).getCourse().getSemester().getName())
                .scheduleItems(scheduleItems)
                .build();
    }

    @Transactional
    public Enrollment registerCourse(String studentId, String courseId) {
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new BusinessException("Registration is not open."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getSemester().getId().equals(activePeriod.getSemester().getId())) {
            throw new BusinessException("This course is not in the active registration period.");
        }

        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new CourseFullException(course.getCourseCode());
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                studentId, course.getSemester().getId(), course.getSubject().getId()
        );
        if (alreadyEnrolled) {
            throw new AlreadyEnrolledException(course.getSubject().getName());
        }

        checkPrerequisites(student, course.getSubject());

        course.setCurrentStudents(course.getCurrentStudents() + 1);
        courseRepository.save(course);

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .registrationTime(LocalDateTime.now())
                .status(EnrollmentStatus.REGISTERED)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void dropCourse(String studentId, String enrollmentId) {
        registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new BusinessException("Registration is not open."));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new BusinessException("You are not authorized to drop this course.");
        }

        Course course = enrollment.getCourse();
        course.setCurrentStudents(course.getCurrentStudents() - 1);
        courseRepository.save(course);

        enrollmentRepository.delete(enrollment);
    }

    private void checkPrerequisites(Student student, Subject subject) {
        Set<Subject> prerequisites = subject.getPrerequisites();
        if (prerequisites.isEmpty()) {
            return;
        }

        Set<String> passedSubjectIds = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .filter(e -> e.getGrade() != null && e.getGrade() >= 4.0)
                .map(e -> e.getCourse().getSubject().getId())
                .collect(Collectors.toSet());

        for (Subject prereq : prerequisites) {
            if (!passedSubjectIds.contains(prereq.getId())) {
                throw new PrerequisiteNotMetException("Prerequisite not met: You must pass " + prereq.getName());
            }
        }
    }
}