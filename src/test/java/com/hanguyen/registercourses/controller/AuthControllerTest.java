package com.hanguyen.registercourses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanguyen.registercourses.config.TestContainersConfig;
import com.hanguyen.registercourses.dto.request.AuthRequest;
import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.entity.Major;
import com.hanguyen.registercourses.repository.DepartmentRepository;
import com.hanguyen.registercourses.repository.MajorRepository;
import com.hanguyen.registercourses.entity.Department;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        MajorRepository majorRepository;

        @Autowired
        DepartmentRepository departmentRepository;

        Major testMajor;
        static AtomicInteger counter = new AtomicInteger(0);

        private String shortUsername(String prefix) {
                return prefix + counter.incrementAndGet();
        }

        @BeforeEach
        void setUp() {
                Department dept = departmentRepository.findAll().stream().findFirst()
                                .orElseGet(() -> departmentRepository
                                                .save(Department.builder().name("Test Dept").build()));
                testMajor = majorRepository.findAll().stream().findFirst()
                                .orElseGet(() -> majorRepository.save(Major.builder()
                                                .name("Computer Science")
                                                .department(dept)
                                                .build()));
        }

        @Test
        @DisplayName("POST /auth/register - should register ADMIN and set cookies")
        void register_shouldSetCookiesAndReturnSuccess() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .username(shortUsername("adm"))
                                .password("password123")
                                .firstname("Test")
                                .lastname("Admin")
                                .dod(LocalDate.of(2000, 1, 1))
                                .role(Roles.ADMIN)
                                .build();

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.authenticated").value(true))
                                .andExpect(cookie().exists("ACCESS_TOKEN"))
                                .andExpect(cookie().exists("REFRESH_TOKEN"));
        }

        @Test
        @DisplayName("POST /auth/register - should register STUDENT with major and set cookies")
        void registerStudent_shouldSetCookiesAndReturnSuccess() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .username(shortUsername("stu"))
                                .password("password123")
                                .firstname("Test")
                                .lastname("Student")
                                .dod(LocalDate.of(2000, 1, 1))
                                .role(Roles.STUDENT)
                                .studentCode("S" + counter.get())
                                .enrollmentYear(2024)
                                .majorId(testMajor.getId())
                                .build();

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.authenticated").value(true))
                                .andExpect(cookie().exists("ACCESS_TOKEN"))
                                .andExpect(cookie().exists("REFRESH_TOKEN"));
        }

        @Test
        @DisplayName("POST /auth/login - should login and set cookies")
        void login_shouldSetCookiesAndReturnSuccess() throws Exception {
                String username = shortUsername("log");
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .username(username)
                                .password("password123")
                                .firstname("Login")
                                .lastname("Test")
                                .dod(LocalDate.of(2000, 1, 1))
                                .role(Roles.ADMIN)
                                .build();

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk());

                AuthRequest loginRequest = new AuthRequest();
                loginRequest.setUsername(username);
                loginRequest.setPassword("password123");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.authenticated").value(true))
                                .andExpect(cookie().exists("ACCESS_TOKEN"))
                                .andExpect(cookie().exists("REFRESH_TOKEN"));
        }

        @Test
        @DisplayName("POST /auth/login - should return error for invalid credentials")
        void login_shouldReturnErrorForInvalidCredentials() throws Exception {
                AuthRequest request = new AuthRequest();
                request.setUsername("nonexistent");
                request.setPassword("wrongpassword");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("POST /auth/refresh - should refresh token")
        void refresh_shouldReturnNewAccessToken() throws Exception {
                String username = shortUsername("ref");
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .username(username)
                                .password("password123")
                                .firstname("Refresh")
                                .lastname("Test")
                                .dod(LocalDate.of(2000, 1, 1))
                                .role(Roles.ADMIN)
                                .build();

                MvcResult result = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                Cookie refreshCookie = result.getResponse().getCookie("REFRESH_TOKEN");

                mockMvc.perform(post("/auth/refresh")
                                .cookie(refreshCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(cookie().exists("ACCESS_TOKEN"));
        }

        @Test
        @DisplayName("POST /auth/logout - should clear cookies")
        void logout_shouldClearCookies() throws Exception {
                String username = shortUsername("out");
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .username(username)
                                .password("password123")
                                .firstname("Logout")
                                .lastname("Test")
                                .dod(LocalDate.of(2000, 1, 1))
                                .role(Roles.ADMIN)
                                .build();

                MvcResult result = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                Cookie accessCookie = result.getResponse().getCookie("ACCESS_TOKEN");
                Cookie refreshCookie = result.getResponse().getCookie("REFRESH_TOKEN");

                mockMvc.perform(post("/auth/logout")
                                .cookie(accessCookie, refreshCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }
}
