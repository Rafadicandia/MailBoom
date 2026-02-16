package com.mailboom.api.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.Role;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.user.dto.GetAllUsersRequest;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.TokenEntity;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.TokenRepository;
import com.mailboom.api.infrastructure.security.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private String jwtToken;
    private User adminUser;

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

        // Create Admin User
        adminUser = User.createAdmin(
                UserId.generate(),
                new Email("admin@example.com"),
                new Name("Admin User"),
                new PasswordHash("password")

        );
        userRepository.save(adminUser);

        // Create some regular users
        IntStream.range(0, 15).forEach(i -> {
            User user = User.create(
                    UserId.generate(),
                    new Email("user" + i + "@example.com"),
                    new Name("User " + i),
                    new PasswordHash("password"),
                    new EmailCounter(0)
            );
            userRepository.save(user);
        });

        springDataUserRepository.flush();

        UserEntity userEntity = springDataUserRepository.findByEmail("admin@example.com")
                .orElseThrow(() -> new RuntimeException("Admin not found after save."));

        var userDetails = userDetailsService.loadUserByUsername(adminUser.getEmail().email());
        jwtToken = jwtService.generateToken(userDetails);

        TokenEntity tokenEntity = TokenEntity.builder()
                .user(userEntity)
                .token(jwtToken)
                .tokenType(TokenEntity.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(tokenEntity);
        tokenRepository.flush();
    }

    @Test
    void testGetAllUsers_Pagination() throws Exception {
        GetAllUsersRequest request = new GetAllUsersRequest(adminUser.getId().value().toString());

        mockMvc.perform(get("/api/admin/users/all")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(16)) // 15 users + 1 admin
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllUsers_SecondPage() throws Exception {
        GetAllUsersRequest request = new GetAllUsersRequest(adminUser.getId().value().toString());

        mockMvc.perform(get("/api/admin/users/all")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(6))) // Remaining 6 users
                .andExpect(jsonPath("$.totalElements").value(16))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(1));
    }
}
