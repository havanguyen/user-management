package com.hanguyen.demo_spring_bai1.repository;

import com.hanguyen.demo_spring_bai1.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, String> {
    Optional<RegistrationPeriod> findByIsActiveTrue();
}