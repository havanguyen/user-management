package com.hanguyen.registercourses.repository;
import com.hanguyen.registercourses.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
}