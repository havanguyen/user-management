package com.hanguyen.demo_spring_bai1.service.accademic;

import com.hanguyen.demo_spring_bai1.dto.request.accademic.CourseRequest;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import com.hanguyen.demo_spring_bai1.entity.Semester;
import com.hanguyen.demo_spring_bai1.entity.Subject;
import com.hanguyen.demo_spring_bai1.constant.CourseStatus;
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.LecturerRepository;
import com.hanguyen.demo_spring_bai1.repository.SemesterRepository;
import com.hanguyen.demo_spring_bai1.repository.SubjectRepository;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final LecturerRepository lecturerRepository;

    public Course createCourse(CourseRequest request) {
        log.info("Creating course with code: {}", request.getCourseCode());

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // 2. Tạo đối tượng Course
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .maxStudents(request.getMaxStudents())
                .currentStudents(0) // Mới tạo nên sĩ số là 0
                .scheduleInfo(request.getScheduleInfo())
                .status(CourseStatus.UPCOMING) // Trạng thái ban đầu
                .subject(subject)
                .semester(semester)
                .lecturer(lecturer)
                .build();

        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Page<Course> getCoursesBySemester(String semesterId, int page, int size, String sortBy) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return courseRepository.findBySemesterId(semesterId, pageable);
    }

    public Course getCourseById(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}