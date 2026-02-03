package com.hanguyen.registercourses.repository;

import com.hanguyen.registercourses.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
    Optional<TimeSlot> findByPeriodNumber(Integer periodNumber);

    List<TimeSlot> findAllByIsActiveTrueOrderByPeriodNumber();
}
