package com.hanguyen.registercourses.service.domain;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.CourseSchedule;
import com.hanguyen.registercourses.repository.CourseScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeConflictChecker {
    CourseScheduleRepository courseScheduleRepository;
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
    public List<CourseSchedule> getStudentSchedules(String studentId, String semesterId) {
        return courseScheduleRepository.findStudentSchedulesBySemester(studentId, semesterId);
    }
}
