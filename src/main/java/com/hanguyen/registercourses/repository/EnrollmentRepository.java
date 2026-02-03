package com.hanguyen.registercourses.repository;

import com.hanguyen.registercourses.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
        List<Enrollment> findByStudentIdAndCourseSemesterId(String studentId, String semesterId);

        @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.semester.id = :semesterId")
        List<Enrollment> findByStudentIdAndSemesterId(@Param("studentId") String studentId,
                        @Param("semesterId") String semesterId);

        List<Enrollment> findAllByStudentId(String studentId);

        boolean existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(String studentId, String semesterId,
                        String subjectId);

        boolean existsByStudentIdAndCourseId(String studentId, String courseId);

        @Query("""
                        SELECT DISTINCT e FROM Enrollment e
                        LEFT JOIN FETCH e.student s
                        LEFT JOIN FETCH s.user
                        LEFT JOIN FETCH s.major m
                        LEFT JOIN FETCH m.department
                        LEFT JOIN FETCH e.course c
                        LEFT JOIN FETCH c.lecturer l
                        LEFT JOIN FETCH l.user
                        LEFT JOIN FETCH l.department
                        LEFT JOIN FETCH c.subject sub
                        LEFT JOIN FETCH sub.department
                        WHERE e.course.id = :courseId
                        """)
        List<Enrollment> findAllByCourseIdWithDetails(@Param("courseId") String courseId);

        @Query("""
                        SELECT e FROM Enrollment e
                        WHERE e.course.id = :courseId
                        AND e.status = 'WAITLIST'
                        ORDER BY e.queueOrder ASC
                        """)
        List<Enrollment> findWaitlistByCourseIdOrderByQueueOrder(@Param("courseId") String courseId);

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'WAITLIST'")
        int countWaitlistByCourseId(@Param("courseId") String courseId);

        Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);
}