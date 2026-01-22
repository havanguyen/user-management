package com.hanguyen.registercourses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanguyen.registercourses.config.TestContainersConfig;
import com.hanguyen.registercourses.dto.request.UserCreationRequest;
import com.hanguyen.registercourses.dto.request.UserUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        static AtomicInteger counter = new AtomicInteger(0);

        private String shortUsername(String prefix) {
                return prefix + counter.incrementAndGet();
        }

        @Test
        @DisplayName("POST /users - should create user (ADMIN only)")
        @WithMockUser(roles = "ADMIN")
        void createUser_shouldReturnCreatedUser() throws Exception {
                String username = shortUsername("usr");
                UserCreationRequest request = new UserCreationRequest();
                request.setUsername(username);
                request.setPassword("password123");
                request.setFirstname("New");
                request.setLastname("User");
                request.setDod(LocalDate.of(1995, 5, 15));

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.username").value(username));
        }

        @Test
        @DisplayName("POST /users - should deny access for non-admin")
        @WithMockUser(roles = "USER")
        void createUser_shouldDenyForNonAdmin() throws Exception {
                UserCreationRequest request = new UserCreationRequest();
                request.setUsername(shortUsername("tst"));
                request.setPassword("password123");
                request.setFirstname("Test");
                request.setLastname("User");
                request.setDod(LocalDate.of(1995, 5, 15));

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /users - should return paginated users (ADMIN only)")
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_shouldReturnPaginatedList() throws Exception {
                mockMvc.perform(get("/users")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.items").isArray())
                                .andExpect(jsonPath("$.data.currentPage").value(0))
                                .andExpect(jsonPath("$.data.pageSize").value(10));
        }

        @Test
        @DisplayName("GET /users/{id} - should return user by id (ADMIN only)")
        @WithMockUser(roles = "ADMIN")
        void getUser_shouldReturnUserById() throws Exception {
                String username = shortUsername("fnd");
                UserCreationRequest request = new UserCreationRequest();
                request.setUsername(username);
                request.setPassword("password123");
                request.setFirstname("Find");
                request.setLastname("User");
                request.setDod(LocalDate.of(1995, 5, 15));

                MvcResult result = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andReturn();

                String userId = objectMapper.readTree(result.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                mockMvc.perform(get("/users/{userId}", userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.username").value(username));
        }

        @Test
        @DisplayName("PUT /users/{id} - should update user (ADMIN only)")
        @WithMockUser(roles = "ADMIN")
        void updateUser_shouldReturnUpdatedUser() throws Exception {
                String username = shortUsername("upd");
                UserCreationRequest createRequest = new UserCreationRequest();
                createRequest.setUsername(username);
                createRequest.setPassword("password123");
                createRequest.setFirstname("Update");
                createRequest.setLastname("User");
                createRequest.setDod(LocalDate.of(1995, 5, 15));

                MvcResult result = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String userId = objectMapper.readTree(result.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                UserUpdateRequest updateRequest = new UserUpdateRequest();
                updateRequest.setFirstname("Updated");
                updateRequest.setLastname("Name");

                mockMvc.perform(put("/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.firstname").value("Updated"));
        }

        @Test
        @DisplayName("DELETE /users/{id} - should delete user (ADMIN only)")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_shouldReturnSuccess() throws Exception {
                String username = shortUsername("del");
                UserCreationRequest request = new UserCreationRequest();
                request.setUsername(username);
                request.setPassword("password123");
                request.setFirstname("Delete");
                request.setLastname("User");
                request.setDod(LocalDate.of(1995, 5, 15));

                MvcResult result = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andReturn();

                String userId = objectMapper.readTree(result.getResponse().getContentAsString())
                                .path("data").path("id").asText();

                mockMvc.perform(delete("/users/{userId}", userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }
}
