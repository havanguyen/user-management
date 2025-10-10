package com.hanguyen.demo_spring_bai1.repository;

import com.hanguyen.demo_spring_bai1.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {}