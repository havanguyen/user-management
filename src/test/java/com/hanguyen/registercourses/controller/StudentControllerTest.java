package com.hanguyen.registercourses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanguyen.registercourses.config.TestContainersConfig;
import com.hanguyen.registercourses.constant.CourseStatus;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
@Transactional
class StudentControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        StudentRepository studentRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        CourseRepository courseRepository;

        @Autowired
        SemesterRepository semesterRepository;

        @Autowired
        SubjectRepository subjectRepository;

        @Autowired
        DepartmentRepository departmentRepository;

        @Autowired
        LecturerRepository lecturerRepository;

        @Autowired
        RegistrationPeriodRepository registrationPeriodRepository;

        @Autowired
        MajorRepository majorRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @BeforeEach
        void setUp() {
                Department dept = departmentRepository.save(Department.builder()
                                .name("CS Dept " + System.currentTimeMillis())
                                .build());

                Semester semester = semesterRepository.save(Semester.builder()
                                .name("Test Semester " + System.currentTimeMillis())
                                .startDate(LocalDate.now().plusDays(30))
                                .endDate(LocalDate.now().plusDays(150))
                                .build());

                Subject subject = subjectRepository.save(Subject.builder()
                                .subjectCode("SUB" + System.currentTimeMillis())
                                .name("Test Subject")
                                .credits(3)
                                .department(dept)
                                .build());

                Set<Roles> lecRoles = new HashSet<>();
                lecRoles.add(Roles.LECTURER);
                User lecUser = userRepository.save(User.builder()
                                .username("lec" + System.currentTimeMillis())
                                .password(passwordEncoder.encode("password"))
                                .firstname("Test")
                                .lastname("Lecturer")
                                .roles(lecRoles)
                                .build());

                Lecturer lecturer = lecturerRepository.save(Lecturer.builder()
                                .user(lecUser)
                                .department(dept)
                                .build());

                registrationPeriodRepository.save(RegistrationPeriod.builder()
                                .semester(semester)
                                .startTime(LocalDateTime.now().minusDays(1))
                                .endTime(LocalDateTime.now().plusDays(30))
                                .isActive(true)
                                .build());

                courseRepository.save(Course.builder()
                                .courseCode("CRS" + System.currentTimeMillis())
                                .maxStudents(30)
                                .currentStudents(0)
                                .scheduleInfo("Mon/Wed 9:00")
                                .status(CourseStatus.OPEN_FOR_REGISTRATION)
                                .subject(subject)
                                .semester(semester)
                                .lecturer(lecturer)
                                .build());
        }

        @Test
        @DisplayName("GET /api/student/courses/open-for-registration - should return open courses")
        @WithMockUser(roles = "STUDENT")
        void getOpenCourses_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/api/student/courses/open-for-registration")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray());
        }

        @Test
        @DisplayName("Student endpoints - should deny access for non-student")
        @WithMockUser(roles = "USER")
        void studentEndpoints_shouldDenyForNonStudent() throws Exception {
                mockMvc.perform(get("/api/student/courses/open-for-registration"))
                                .andExpect(status().isForbidden());
        }
}
