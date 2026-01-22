package com.hanguyen.registercourses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanguyen.registercourses.config.TestContainersConfig;
import com.hanguyen.registercourses.dto.request.CourseRequest;
import com.hanguyen.registercourses.dto.request.RegistrationPeriodRequest;
import com.hanguyen.registercourses.dto.request.SemesterRequest;
import com.hanguyen.registercourses.dto.request.SubjectRequest;
import com.hanguyen.registercourses.entity.Department;
import com.hanguyen.registercourses.entity.Lecturer;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.repository.DepartmentRepository;
import com.hanguyen.registercourses.repository.LecturerRepository;
import com.hanguyen.registercourses.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
class AdminControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        DepartmentRepository departmentRepository;

        @Autowired
        LecturerRepository lecturerRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        Department testDepartment;
        Lecturer testLecturer;

        @BeforeEach
        void setUp() {
                testDepartment = departmentRepository.save(Department.builder()
                                .name("Test Department " + System.currentTimeMillis())
                                .build());

                Set<Roles> roles = new HashSet<>();
                roles.add(Roles.LECTURER);
                User lecturerUser = userRepository.save(User.builder()
                                .username("lecturer" + System.currentTimeMillis())
                                .password(passwordEncoder.encode("password"))
                                .firstname("Test")
                                .lastname("Lecturer")
                                .roles(roles)
                                .build());

                testLecturer = lecturerRepository.save(Lecturer.builder()
                                .user(lecturerUser)
                                .department(testDepartment)
                                .build());
        }

        @Test
        @DisplayName("POST /api/admin/semesters - should create semester")
        @WithMockUser(roles = "ADMIN")
        void createSemester_shouldReturnCreatedSemester() throws Exception {
                SemesterRequest request = new SemesterRequest();
                request.setName("Semester " + System.currentTimeMillis());
                request.setStartDate(LocalDate.now().plusDays(30));
                request.setEndDate(LocalDate.now().plusDays(150));

                mockMvc.perform(post("/api/admin/semesters")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.name").value(request.getName()));
        }

        @Test
        @DisplayName("GET /api/admin/semesters - should return paginated semesters")
        @WithMockUser(roles = "ADMIN")
        void getAllSemesters_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/api/admin/semesters")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray());
        }

        @Test
        @DisplayName("POST /api/admin/subjects - should create subject")
        @WithMockUser(roles = "ADMIN")
        void createSubject_shouldReturnCreatedSubject() throws Exception {
                SubjectRequest request = new SubjectRequest();
                request.setSubjectCode("SUB" + System.currentTimeMillis());
                request.setName("Test Subject");
                request.setCredits(3);
                request.setDepartmentId(testDepartment.getId());

                mockMvc.perform(post("/api/admin/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.name").value("Test Subject"));
        }

        @Test
        @DisplayName("GET /api/admin/subjects - should return paginated subjects")
        @WithMockUser(roles = "ADMIN")
        void getAllSubjects_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/api/admin/subjects")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray());
        }

        @Test
        @DisplayName("POST /api/admin/courses - should create course")
        @WithMockUser(roles = "ADMIN")
        void createCourse_shouldReturnCreatedCourse() throws Exception {
                SemesterRequest semRequest = new SemesterRequest();
                semRequest.setName("CourseSem" + System.currentTimeMillis());
                semRequest.setStartDate(LocalDate.now().plusDays(30));
                semRequest.setEndDate(LocalDate.now().plusDays(150));

                MvcResult semResult = mockMvc.perform(post("/api/admin/semesters")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(semRequest)))
                                .andExpect(status().isOk())
                                .andReturn();
                String semesterId = objectMapper.readTree(semResult.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                SubjectRequest subRequest = new SubjectRequest();
                subRequest.setSubjectCode("CS" + System.currentTimeMillis());
                subRequest.setName("Computer Science");
                subRequest.setCredits(4);
                subRequest.setDepartmentId(testDepartment.getId());

                MvcResult subResult = mockMvc.perform(post("/api/admin/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(subRequest)))
                                .andExpect(status().isOk())
                                .andReturn();
                String subjectId = objectMapper.readTree(subResult.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                CourseRequest request = new CourseRequest();
                request.setCourseCode("CRS" + System.currentTimeMillis());
                request.setMaxStudents(30);
                request.setScheduleInfo("Mon/Wed 9:00-10:30");
                request.setSubjectId(subjectId);
                request.setSemesterId(semesterId);
                request.setLecturerId(testLecturer.getId());

                mockMvc.perform(post("/api/admin/courses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.courseCode").value(request.getCourseCode()));
        }

        @Test
        @DisplayName("GET /api/admin/courses - should return paginated courses")
        @WithMockUser(roles = "ADMIN")
        void getAllCourses_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/api/admin/courses")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray());
        }

        @Test
        @DisplayName("POST /api/admin/registration-periods - should create period")
        @WithMockUser(roles = "ADMIN")
        void createRegistrationPeriod_shouldReturnCreatedPeriod() throws Exception {
                SemesterRequest semRequest = new SemesterRequest();
                semRequest.setName("PeriodSem" + System.currentTimeMillis());
                semRequest.setStartDate(LocalDate.now().plusDays(30));
                semRequest.setEndDate(LocalDate.now().plusDays(150));

                MvcResult semResult = mockMvc.perform(post("/api/admin/semesters")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(semRequest)))
                                .andExpect(status().isOk())
                                .andReturn();
                String semesterId = objectMapper.readTree(semResult.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                RegistrationPeriodRequest request = new RegistrationPeriodRequest();
                request.setSemesterId(semesterId);
                request.setStartTime(LocalDateTime.now().plusDays(1));
                request.setEndTime(LocalDateTime.now().plusDays(14));

                mockMvc.perform(post("/api/admin/registration-periods")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        @DisplayName("GET /api/admin/registration-periods - should return paginated periods")
        @WithMockUser(roles = "ADMIN")
        void getAllRegistrationPeriods_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/api/admin/registration-periods")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray());
        }

        @Test
        @DisplayName("Admin endpoints - should deny access for non-admin")
        @WithMockUser(roles = "USER")
        void adminEndpoints_shouldDenyForNonAdmin() throws Exception {
                mockMvc.perform(get("/api/admin/semesters"))
                                .andExpect(status().isForbidden());
        }
}
