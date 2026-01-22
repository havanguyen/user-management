package com.hanguyen.registercourses.service.domain;

import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.CourseSchedule;
import com.hanguyen.registercourses.repository.CourseScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain Service for checking time conflicts between course schedules
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeConflictChecker {

    CourseScheduleRepository courseScheduleRepository;

    /**
     * Kiểm tra xem sinh viên có bị trùng lịch học với môn mới đăng ký không
     *
     * @param studentId ID của sinh viên
     * @param course    Môn học mới đăng ký
     * @return true nếu có trùng lịch
     */
    public boolean hasTimeConflict(String studentId, Course course) {
        if (course.getSchedules() == null || course.getSchedules().isEmpty()) {
            return false;
        }

        String semesterId = course.getSemester().getId();

        for (CourseSchedule schedule : course.getSchedules()) {
            boolean hasConflict = courseScheduleRepository.existsConflictingSchedule(
                    studentId,
                    semesterId,
                    schedule.getDayOfWeek(),
                    schedule.getStartPeriod(),
                    schedule.getEndPeriod());

            if (hasConflict) {
                return true;
            }
        }

        return false;
    }

    /**
     * Lấy danh sách lịch học của sinh viên trong một kỳ
     */
    public List<CourseSchedule> getStudentSchedules(String studentId, String semesterId) {
        return courseScheduleRepository.findStudentSchedulesBySemester(studentId, semesterId);
    }
}
