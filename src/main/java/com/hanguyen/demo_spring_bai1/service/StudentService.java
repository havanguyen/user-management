package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.response.CourseResponse;
import com.hanguyen.demo_spring_bai1.dto.response.MyScheduleResponse;
import com.hanguyen.demo_spring_bai1.entity.*;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.mapper.CourseMapper;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.EnrollmentRepository;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
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
public class StudentService {

        private final CourseRepository courseRepository;
        private final RegistrationPeriodRepository registrationPeriodRepository;
        private final StudentRepository studentRepository;
        private final EnrollmentRepository enrollmentRepository;
        private final CourseMapper courseMapper; // Inject mapper

        @Transactional(readOnly = true)
        public Page<CourseResponse> getOpenCoursesForRegistration(int page, int size, String sortBy, String direction) {
                RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));
                String semesterId = activePeriod.getSemester().getId();

                Sort sort = direction.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();

                Pageable pageable = PageRequest.of(page, size, sort);

                Page<Course> courses = courseRepository.findBySemesterId(semesterId, pageable);

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