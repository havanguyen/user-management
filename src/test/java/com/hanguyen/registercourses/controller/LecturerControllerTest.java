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
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
@Transactional
class LecturerControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        LecturerRepository lecturerRepository;

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
        PasswordEncoder passwordEncoder;

        Lecturer testLecturer;

        @BeforeEach
        void setUp() {
                Department dept = departmentRepository.save(Department.builder()
                                .name("Eng" + System.nanoTime())
                                .build());

                Semester semester = semesterRepository.save(Semester.builder()
                                .name("Fall" + System.nanoTime())
                                .startDate(LocalDate.now().plusDays(30))
                                .endDate(LocalDate.now().plusDays(150))
                                .build());

                Subject subject = subjectRepository.save(Subject.builder()
                                .subjectCode("E" + System.nanoTime())
                                .name("Engineering")
                                .credits(3)
                                .department(dept)
                                .build());

                Set<Roles> roles = new HashSet<>();
                roles.add(Roles.LECTURER);
                User lecturerUser = userRepository.save(User.builder()
                                .username("lec" + System.nanoTime())
                                .password(passwordEncoder.encode("password"))
                                .firstname("Test")
                                .lastname("Lecturer")
                                .roles(roles)
                                .build());

                testLecturer = lecturerRepository.save(Lecturer.builder()
                                .user(lecturerUser)
                                .department(dept)
                                .build());

                courseRepository.save(Course.builder()
                                .courseCode("L" + System.nanoTime())
                                .maxStudents(25)
                                .currentStudents(0)
                                .scheduleInfo("Tue/Thu 14:00")
                                .status(CourseStatus.OPEN_FOR_REGISTRATION)
                                .subject(subject)
                                .semester(semester)
                                .lecturer(testLecturer)
                                .build());
        }

        @Test
        @DisplayName("Lecturer endpoints - should deny access for non-lecturer role")
        @WithMockUser(roles = "USER")
        void lecturerEndpoints_shouldDenyForNonLecturer() throws Exception {
                mockMvc.perform(get("/api/lecturer/{lecturerId}/courses", testLecturer.getId()))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Lecturer endpoints - should deny access for student role")
        @WithMockUser(roles = "STUDENT")
        void lecturerEndpoints_shouldDenyForStudent() throws Exception {
                mockMvc.perform(get("/api/lecturer/{lecturerId}/courses", testLecturer.getId()))
                                .andExpect(status().isForbidden());
        }
}
