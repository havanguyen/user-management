package com.hanguyen.registercourses.controller;

import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.dto.response.DepartmentResponse;
import com.hanguyen.registercourses.dto.response.MajorResponse;
import com.hanguyen.registercourses.repository.DepartmentRepository;
import com.hanguyen.registercourses.repository.MajorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicController {

    MajorRepository majorRepository;
    DepartmentRepository departmentRepository;

    @GetMapping("/majors")
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        List<MajorResponse> majors = majorRepository.findAll().stream()
                .map(major -> MajorResponse.builder()
                        .id(major.getId())
                        .name(major.getName())
                        .majorCode(major.getMajorCode())
                        .pricePerCredit(major.getPricePerCredit())
                        .departmentId(major.getDepartment().getId())
                        .departmentName(major.getDepartment().getName())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.buildSuccessResponse(
                majors,
                SuccessCode.GET_ALL_MAJORS_SUCCESSFUL);
    }

    @GetMapping("/departments")
    public ApiResponse<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentRepository.findAll().stream()
                .map(dept -> DepartmentResponse.builder()
                        .id(dept.getId())
                        .name(dept.getName())
                        .departmentCode(dept.getDepartmentCode())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.buildSuccessResponse(
                departments,
                SuccessCode.GET_ALL_DEPARTMENTS_SUCCESSFUL);
    }
}
