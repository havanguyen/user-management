package com.hanguyen.registercourses.service.accademic;

import com.hanguyen.registercourses.dto.request.CourseRequest;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.Lecturer;
import com.hanguyen.registercourses.entity.Semester;
import com.hanguyen.registercourses.entity.Subject;
import com.hanguyen.registercourses.constant.CourseStatus;
import com.hanguyen.registercourses.repository.CourseRepository;
import com.hanguyen.registercourses.repository.LecturerRepository;
import com.hanguyen.registercourses.repository.SemesterRepository;
import com.hanguyen.registercourses.repository.SubjectRepository;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {
    CourseRepository courseRepository;
    SubjectRepository subjectRepository;
    SemesterRepository semesterRepository;
    LecturerRepository lecturerRepository;

    @CacheEvict(value = "courses", allEntries = true)
    public Course createCourse(CourseRequest request) {
        log.info("Creating course with code: {}", request.getCourseCode());

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .maxStudents(request.getMaxStudents())
                .currentStudents(0)
                .scheduleInfo(request.getScheduleInfo())
                .status(CourseStatus.UPCOMING)
                .subject(subject)
                .semester(semester)
                .lecturer(lecturer)
                .build();

        return courseRepository.save(course);
    }

    public Page<Course> getAllCourses(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return courseRepository.findAll(pageable);
    }

    @Cacheable(value = "courses", key = "#semesterId + '-' + #page + '-' + #size")
    public Page<Course> getCoursesBySemester(String semesterId, int page, int size, String sortBy) {
        log.info("Fetching courses for semester {} from database (not cached)", semesterId);
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