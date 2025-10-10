package com.hanguyen.demo_spring_bai1.service.accademic;

import com.hanguyen.demo_spring_bai1.dto.request.accademic.RegistrationPeriodRequest;
import com.hanguyen.demo_spring_bai1.entity.RegistrationPeriod;
import com.hanguyen.demo_spring_bai1.entity.Semester;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationPeriodService {
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final SemesterRepository semesterRepository;

    public RegistrationPeriod createPeriod(RegistrationPeriodRequest request) {
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        RegistrationPeriod period = RegistrationPeriod.builder()
                .semester(semester)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(false) // Mặc định là chưa active
                .build();

        return registrationPeriodRepository.save(period);
    }

    public RegistrationPeriod setActivePeriod(String periodId, boolean isActive) {
        RegistrationPeriod period = registrationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationPeriod", "id", periodId));

        // Logic nghiệp vụ: Đảm bảo chỉ có một kỳ đăng ký được active tại một thời điểm
        if (isActive) {
            Optional<RegistrationPeriod> currentActivePeriod = registrationPeriodRepository.findByIsActiveTrue();
            if (currentActivePeriod.isPresent() && !currentActivePeriod.get().getId().equals(periodId)) {
                throw new BusinessException("Another registration period is already active. Please deactivate it first.");
            }
        }

        period.setActive(isActive);
        return registrationPeriodRepository.save(period);
    }

    public List<RegistrationPeriod> getAllPeriods() {
        return registrationPeriodRepository.findAll();
    }
}