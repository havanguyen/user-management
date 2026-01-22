package com.hanguyen.registercourses.repository;
import com.hanguyen.registercourses.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {}