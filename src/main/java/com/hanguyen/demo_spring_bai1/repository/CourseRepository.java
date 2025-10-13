package com.hanguyen.demo_spring_bai1.repository;

import com.hanguyen.demo_spring_bai1.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // Gay ra loi N + 1
    //List<Course> findBySemesterId(String semesterId);

    List<Course> findByLecturerIdAndSemesterId(String lecturerId, String semesterId);



    @EntityGraph(attributePaths = {
            "lecturer", "lecturer.user", "lecturer.user.roles",  // Fetch roles (dù là collection)
            "lecturer.department",
            "subject", "subject.department", "subject.prerequisites"  // Fetch ManyToMany
    })
    @Query("SELECT c FROM Course c WHERE c.semester.id = :semesterId")  // Không cần JOIN FETCH nữa
    List<Course> findBySemesterId(@Param("semesterId") String semesterId);

    // --- GIẢI PHÁP 2: ENTITY GRAPH ---
    @EntityGraph(value = "course-with-details")
    List<Course> findAllBySemesterId(String semesterId);
}