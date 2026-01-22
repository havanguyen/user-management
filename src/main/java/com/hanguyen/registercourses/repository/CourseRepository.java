package com.hanguyen.registercourses.repository;

import com.hanguyen.registercourses.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findByLecturerId(String lecturerId);

    List<Course> findByLecturerIdAndSemesterId(String lecturerId, String semesterId);

    @EntityGraph(value = "course-with-details")
    Page<Course> findBySemesterId(String semesterId, Pageable pageable);

    @EntityGraph(value = "course-with-details")
    List<Course> findBySemesterId(String semesterId);
}