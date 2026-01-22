package com.hanguyen.registercourses.service.accademic;
import com.hanguyen.registercourses.dto.request.RegistrationPeriodRequest;
import com.hanguyen.registercourses.entity.RegistrationPeriod;
import com.hanguyen.registercourses.entity.Semester;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.RegistrationPeriodRepository;
import com.hanguyen.registercourses.repository.SemesterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationPeriodService {
    RegistrationPeriodRepository registrationPeriodRepository;
    SemesterRepository semesterRepository;
    public RegistrationPeriod createPeriod(RegistrationPeriodRequest request) {
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        RegistrationPeriod period = RegistrationPeriod.builder()
                .semester(semester)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(false)
                .build();
        return registrationPeriodRepository.save(period);
    }
    public RegistrationPeriod setActivePeriod(String periodId, boolean isActive) {
        RegistrationPeriod period = registrationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        if (isActive) {
            Optional<RegistrationPeriod> currentActivePeriod = registrationPeriodRepository.findByIsActiveTrue();
            if (currentActivePeriod.isPresent() && !currentActivePeriod.get().getId().equals(periodId)) {
                throw new AppException(ErrorCode.ANOTHER_PERIOD_ACTIVE);
            }
        }
        period.setActive(isActive);
        return registrationPeriodRepository.save(period);
    }
    public Page<RegistrationPeriod> getAllPeriods(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return registrationPeriodRepository.findAll(pageable);
    }
}