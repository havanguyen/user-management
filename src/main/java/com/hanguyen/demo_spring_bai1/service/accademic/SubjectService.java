package com.hanguyen.demo_spring_bai1.service.accademic;

import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.SubjectRequest;
import com.hanguyen.demo_spring_bai1.entity.Department;
import com.hanguyen.demo_spring_bai1.entity.Subject;
import com.hanguyen.demo_spring_bai1.repository.DepartmentRepository;
import com.hanguyen.demo_spring_bai1.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;

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

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(String id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}