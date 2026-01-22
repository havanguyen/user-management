package com.hanguyen.registercourses.service.accademic;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.dto.request.SubjectRequest;
import com.hanguyen.registercourses.entity.Department;
import com.hanguyen.registercourses.entity.Subject;
import com.hanguyen.registercourses.repository.DepartmentRepository;
import com.hanguyen.registercourses.repository.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectService {
    SubjectRepository subjectRepository;
    DepartmentRepository departmentRepository;
    public Subject createSubject(SubjectRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Set<Subject> prerequisites = new HashSet<>();
        if (request.getPrerequisiteIds() != null && !request.getPrerequisiteIds().isEmpty()) {
            prerequisites = new HashSet<>(subjectRepository.findAllById(request.getPrerequisiteIds()));
        }
        Subject subject = Subject.builder()
                .subjectCode(request.getSubjectCode())
                .name(request.getName())
                .credits(request.getCredits())
                .department(department)
                .prerequisites(prerequisites)
                .build();
        return subjectRepository.save(subject);
    }
    public Page<Subject> getAllSubjects(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return subjectRepository.findAll(pageable);
    }
    public Subject getSubjectById(String id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}