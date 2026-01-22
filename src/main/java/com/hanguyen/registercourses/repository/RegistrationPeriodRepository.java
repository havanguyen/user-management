package com.hanguyen.registercourses.repository;
import com.hanguyen.registercourses.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, String> {
    Optional<RegistrationPeriod> findByIsActiveTrue();
}