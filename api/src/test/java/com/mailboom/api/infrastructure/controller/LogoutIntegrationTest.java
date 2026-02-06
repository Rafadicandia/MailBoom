package com.mailboom.api.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailboom.api.infrastructure.user.dto.LoginRequest;
import com.mailboom.api.infrastructure.user.dto.NewUserRequest;
import com.mailboom.api.infrastructure.user.dto.TokenResponse;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class LogoutIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("application.security.jwt.secret-key", () -> "c2VjcmV0X2tleV9mb3JfanNvbl93ZWJfZG9rZW5zX2FuZF9zZWN1cmluZ19hcHBsaWNhdGlvbg==");
        registry.add("application.security.jwt.expiration", () -> "86400000"); // 24 hours
        registry.add("application.security.jwt.refresh-token.expiration", () -> "604800000"); // 7 days
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {

        NewUserRequest registerRequest = new NewUserRequest("test@example.com", "password123", "Test User");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());


        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponse tokenResponse = objectMapper.readValue(loginResponse, TokenResponse.class);
        String accessToken = tokenResponse.accessToken();


        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        var token = tokenRepository.findByToken(accessToken);
        assertThat(token).isPresent();
        assertThat(token.get().isRevoked()).isTrue();
        assertThat(token.get().isExpired()).isTrue();
    }
}
