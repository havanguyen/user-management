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

    /**
     * Tìm các lịch học của sinh viên dựa trên các enrollment đã đăng ký
     */
    @Query("""
            SELECT cs FROM CourseSchedule cs
            JOIN cs.course c
            JOIN Enrollment e ON e.course.id = c.id
            WHERE e.student.id = :studentId
            AND e.status = 'REGISTERED'
            AND c.semester.id = :semesterId
            """)
    List<CourseSchedule> findStudentSchedulesBySemester(
            @Param("studentId") String studentId,
            @Param("semesterId") String semesterId);

    /**
     * Kiểm tra xem sinh viên đã có lịch học trùng hay chưa
     */
    @Query("""
            SELECT COUNT(cs) > 0 FROM CourseSchedule cs
            JOIN cs.course c
            JOIN Enrollment e ON e.course.id = c.id
            WHERE e.student.id = :studentId
            AND e.status = 'REGISTERED'
            AND c.semester.id = :semesterId
            AND cs.dayOfWeek = :dayOfWeek
            AND cs.startPeriod <= :endPeriod
            AND cs.endPeriod >= :startPeriod
            """)
    boolean existsConflictingSchedule(
            @Param("studentId") String studentId,
            @Param("semesterId") String semesterId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startPeriod") Integer startPeriod,
            @Param("endPeriod") Integer endPeriod);
}
