package com.hanguyen.registercourses.repository;

import com.hanguyen.registercourses.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, String> {
        List<CourseSchedule> findByCourseId(String courseId);

        @Query("""
                        SELECT cs FROM CourseSchedule cs
                        JOIN FETCH cs.startTimeSlot
                        JOIN FETCH cs.endTimeSlot
                        JOIN cs.course c
                        JOIN Enrollment e ON e.course.id = c.id
                        WHERE e.student.id = :studentId
                        AND e.status = 'REGISTERED'
                        AND c.semester.id = :semesterId
                        """)
        List<CourseSchedule> findStudentSchedulesBySemester(
                        @Param("studentId") String studentId,
                        @Param("semesterId") String semesterId);

        @Query("""
                        SELECT COUNT(cs) > 0 FROM CourseSchedule cs
                        JOIN cs.course c
                        JOIN Enrollment e ON e.course.id = c.id
                        WHERE e.student.id = :studentId
                        AND e.status = 'REGISTERED'
                        AND c.semester.id = :semesterId
                        AND cs.dayOfWeek = :dayOfWeek
                        AND cs.startTimeSlot.periodNumber <= :endPeriod
                        AND cs.endTimeSlot.periodNumber >= :startPeriod
                        """)
        boolean existsConflictingSchedule(
                        @Param("studentId") String studentId,
                        @Param("semesterId") String semesterId,
                        @Param("dayOfWeek") Integer dayOfWeek,
                        @Param("startPeriod") Integer startPeriod,
                        @Param("endPeriod") Integer endPeriod);
}
