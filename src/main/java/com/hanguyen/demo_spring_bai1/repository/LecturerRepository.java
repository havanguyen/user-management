package com.hanguyen.demo_spring_bai1.repository;

import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {

}