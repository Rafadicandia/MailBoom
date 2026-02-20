package com.mailboom.api.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailboom.api.infrastructure.user.dto.LoginRequest;
import com.mailboom.api.infrastructure.user.dto.NewUserRequest;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // JWT Properties
        registry.add("application.security.jwt.secret-key", () -> "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        registry.add("application.security.jwt.expiration", () -> 86400000);
        registry.add("application.security.jwt.refresh-token.expiration", () -> 604800000);

        // AWS Properties (Mocked)
        registry.add("spring.cloud.aws.region.static", () -> "us-east-1");
        registry.add("spring.cloud.aws.credentials.access-key", () -> "test");
        registry.add("spring.cloud.aws.credentials.secret-key", () -> "test");
        registry.add("AWS_REGION", () -> "us-east-1");
        registry.add("AWS_ACCESS_KEY_ID", () -> "test");
        registry.add("AWS_SECRET_ACCESS_KEY", () -> "test");
        registry.add("mailboom.domain.arn", () -> "arn:aws:ses:us-east-1:123456789012:identity/example.com");
    }

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        springDataUserRepository.deleteAll();
    }

    @Test
    void testRegisterAdmin_Success_WhenNoUsersExist() throws Exception {
        NewUserRequest request = new NewUserRequest(
                "admin@example.com",
                "password123",
                "Admin User"
        );

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.user_id").exists());
    }

    @Test
    void testRegisterAdmin_Fails_WhenUsersAlreadyExist() throws Exception {
        // First, create a regular user (not admin)
        NewUserRequest regularUserRequest = new NewUserRequest(
                "user@example.com",
                "password123",
                "Regular User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regularUserRequest)))
                .andExpect(status().isOk());

        // Now try to register admin - should fail
        NewUserRequest adminRequest = new NewUserRequest(
                "admin@example.com",
                "password123",
                "Admin User"
        );

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testRegister_Success() throws Exception {
        NewUserRequest request = new NewUserRequest(
                "newuser@example.com",
                "password123",
                "New User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.user_id").exists());
    }

    @Test
    void testRegister_DuplicateEmail_Fails() throws Exception {
        NewUserRequest request = new NewUserRequest(
                "duplicate@example.com",
                "password123",
                "First User"
        );

        // First registration - should succeed
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Second registration with same email - should fail
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testLogin_WithValidCredentials() throws Exception {
        // Create user directly in repository to avoid transaction issues
        // Use BCrypt encoded password "password123"
        springDataUserRepository.save(
                com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .email("logintest@example.com")
                        .name("Test User")
                        .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")  // BCrypt for "password123"
                        .role(com.mailboom.api.domain.model.user.valueobjects.Role.USER)
                        .plan(com.mailboom.api.domain.model.user.valueobjects.PlanType.FREE)
                        .emailsSentThisMonth(0)
                        .build()
        );
        springDataUserRepository.flush();

        LoginRequest loginRequest = new LoginRequest(
                "logintest@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.user_id").exists());
    }

    @Test
    void testLogin_WrongPassword_Fails() throws Exception {
        // Create user with a BCrypt password
        springDataUserRepository.save(
                com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .email("wrongpass@example.com")
                        .name("Test User")
                        .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                        .role(com.mailboom.api.domain.model.user.valueobjects.Role.USER)
                        .plan(com.mailboom.api.domain.model.user.valueobjects.PlanType.FREE)
                        .emailsSentThisMonth(0)
                        .build()
        );
        springDataUserRepository.flush();

        LoginRequest loginRequest = new LoginRequest(
                "wrongpass@example.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_NonExistentUser_Fails() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "nonexistent@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
