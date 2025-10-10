package com.hanguyen.demo_spring_bai1.repository;

import com.hanguyen.demo_spring_bai1.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findByStudentIdAndCourseSemesterId(String studentId, String semesterId);

    List<Enrollment> findAllByStudentId(String studentId);

    boolean existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(String studentId, String semesterId, String subjectId);

    List<Enrollment> findAllByCourseId(String courseId);
}