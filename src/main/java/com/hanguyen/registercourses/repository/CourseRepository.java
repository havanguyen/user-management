package com.hanguyen.registercourses.repository;

import com.hanguyen.registercourses.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByLecturerId(String lecturerId);

    List<Course> findByLecturerIdAndSemesterId(String lecturerId, String semesterId);

    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.subject s
            LEFT JOIN FETCH s.department
            LEFT JOIN FETCH s.prerequisites p
            LEFT JOIN FETCH p.department
            LEFT JOIN FETCH c.semester
            LEFT JOIN FETCH c.lecturer l
            LEFT JOIN FETCH l.user
            LEFT JOIN FETCH l.department
            LEFT JOIN FETCH c.schedules
            WHERE c.semester.id = :semesterId
            """)
    List<Course> findBySemesterIdWithDetails(@Param("semesterId") String semesterId);

    @Query(value = """
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.subject s
            LEFT JOIN FETCH s.department
            LEFT JOIN FETCH c.semester
            LEFT JOIN FETCH c.lecturer l
            LEFT JOIN FETCH l.user
            LEFT JOIN FETCH l.department
            LEFT JOIN FETCH c.schedules
            WHERE c.semester.id = :semesterId
            """, countQuery = "SELECT COUNT(c) FROM Course c WHERE c.semester.id = :semesterId")
    Page<Course> findBySemesterIdWithDetails(@Param("semesterId") String semesterId, Pageable pageable);
}