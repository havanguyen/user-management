package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.dto.response.CourseResponse;
import com.hanguyen.registercourses.dto.response.MyScheduleResponse;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.mapper.CourseMapper;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.CourseRepository;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import com.hanguyen.registercourses.repository.RegistrationPeriodRepository;
import com.hanguyen.registercourses.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
        private final CourseRepository courseRepository;
        private final RegistrationPeriodRepository registrationPeriodRepository;
        private final StudentRepository studentRepository;
        private final EnrollmentRepository enrollmentRepository;
        private final CourseMapper courseMapper;

        @Transactional(readOnly = true)
        public Page<CourseResponse> getOpenCoursesForRegistration(int page, int size, String sortBy, String direction) {
                RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));
                String semesterId = activePeriod.getSemester().getId();
                Sort sort = direction.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<Course> courses = courseRepository.findBySemesterIdWithDetails(semesterId, pageable);
                return courses.map(courseMapper::toCourseResponse);
        }

        public MyScheduleResponse getMySchedule(String studentId, String semesterId) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
                List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseSemesterId(studentId,
                                semesterId);
                List<MyScheduleResponse.ScheduleItem> scheduleItems = enrollments.stream()
                                .map(enrollment -> {
                                        Course course = enrollment.getCourse();
                                        return MyScheduleResponse.ScheduleItem.builder()
                                                        .courseCode(course.getCourseCode())
                                                        .subjectName(course.getSubject().getName())
                                                        .credits(course.getSubject().getCredits())
                                                        .lecturerName(course.getLecturer().getUser().getFirstname()
                                                                        + " "
                                                                        + course.getLecturer().getUser().getLastname())
                                                        .scheduleInfo(course.getScheduleInfo())
                                                        .build();
                                })
                                .collect(Collectors.toList());
                return MyScheduleResponse.builder()
                                .studentCode(student.getStudentCode())
                                .studentName(student.getUser().getFirstname() + " " + student.getUser().getLastname())
                                .semesterName(enrollments.isEmpty() ? "N/A"
                                                : enrollments.get(0).getCourse().getSemester().getName())
                                .scheduleItems(scheduleItems)
                                .build();
        }
}