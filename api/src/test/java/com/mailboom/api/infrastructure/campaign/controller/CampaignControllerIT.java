package com.mailboom.api.infrastructure.campaign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.campaign.valueobjects.HtmlContent;
import com.mailboom.api.domain.model.campaign.valueobjects.Subject;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.campaign.dto.NewCampaignRequest;
import com.mailboom.api.infrastructure.security.JwtService;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.TokenEntity;
import com.mailboom.api.infrastructure.user.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.user.persistence.jpa.repository.TokenRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CampaignControllerIT {

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
    private ContactListRepository contactListRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    private String jwtToken;
    private User testUser;
    private ContactList testList;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("application.security.jwt.secret-key", () -> "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        registry.add("application.security.jwt.expiration", () -> 86400000);
        registry.add("application.security.jwt.refresh-token.expiration", () -> 604800000);
    }

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        springDataUserRepository.deleteAll();

        testUser = User.create(
                UserId.generate(),
                new Email("campaign_test@hotmail.com"),
                new Name("Campaign Tester"),
                new PasswordHash("password123"),
                new EmailCounter(0)
        );
        userRepository.save(testUser);
        springDataUserRepository.flush();

        UserEntity userEntity = springDataUserRepository.findByEmail("campaign_test@hotmail.com")
                .orElseThrow();

        var userDetails = userDetailsService.loadUserByUsername(testUser.getEmail().email());
        jwtToken = jwtService.generateToken(userDetails);

        TokenEntity tokenEntity = TokenEntity.builder()
                .user(userEntity)
                .token(jwtToken)
                .tokenType(TokenEntity.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(tokenEntity);

        testList = ContactList.create(testUser.getId(), new Name("Recipient List"));
        contactListRepository.save(testList);
    }

    @Test
    void shouldCreateCampaignSuccessfully() throws Exception {
        NewCampaignRequest request = new NewCampaignRequest(
                testUser.getId().value().toString(),
                "Test Subject",
                "<h1>Hello World</h1>",
                "sender@example.com",
                testList.getId().value().toString()
        );

        mockMvc.perform(post("/campaigns/new")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.ownerId").value(testUser.getId().value().toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldGetSentCampaignsFromUserSuccessfully() throws Exception {
        // Given
        Campaign campaign = Campaign.create(
                testUser.getId(),
                new Subject("Sent Campaign"),
                new HtmlContent("<p>Content</p>"),
                "sender@example.com",
                testList.getId()
        );
        campaign = campaign.markAsSending();
        campaign = campaign.markAsSent();
        campaignRepository.save(campaign);

        // When & Then
        mockMvc.perform(get("/campaigns/user/" + testUser.getId().value())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Sent Campaign"))
                .andExpect(jsonPath("$[0].status").value("SENT"));
    }

    @Test
    void shouldDeleteCampaignSuccessfully() throws Exception {
        // Given
        Campaign campaign = Campaign.create(
                testUser.getId(),
                new Subject("To Delete"),
                new HtmlContent("<p>Delete me</p>"),
                "sender@example.com",
                testList.getId()
        );
        campaignRepository.save(campaign);

        // When & Then
        mockMvc.perform(delete("/campaigns/" + campaign.getId().value() + "/delete")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
