package com.hanguyen.demo_spring_bai1.service.accademic;

import com.hanguyen.demo_spring_bai1.dto.request.accademic.RegistrationPeriodRequest;
import com.hanguyen.demo_spring_bai1.entity.RegistrationPeriod;
import com.hanguyen.demo_spring_bai1.entity.Semester;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationPeriodService {
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final SemesterRepository semesterRepository;

    public RegistrationPeriod createPeriod(RegistrationPeriodRequest request) {
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Logic nghiệp vụ: Đảm bảo chỉ có một kỳ đăng ký được active tại một thời điểm
        if (isActive) {
            Optional<RegistrationPeriod> currentActivePeriod = registrationPeriodRepository.findByIsActiveTrue();
            if (currentActivePeriod.isPresent() && !currentActivePeriod.get().getId().equals(periodId)) {
                throw new AppException(ErrorCode.ANOTHER_PERIOD_ACTIVE);
            }
        }

        period.setActive(isActive);
        return registrationPeriodRepository.save(period);
    }

    public List<RegistrationPeriod> getAllPeriods() {
        return registrationPeriodRepository.findAll();
    }
}