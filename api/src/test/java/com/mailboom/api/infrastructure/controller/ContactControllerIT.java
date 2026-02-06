package com.mailboom.api.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.EmailCounter;
import com.mailboom.api.domain.model.user.valueobjects.PasswordHash;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.dto.NewContactListRequest;
import com.mailboom.api.infrastructure.dto.NewContactRequest;
import com.mailboom.api.infrastructure.dto.UpdateContactListRequest;
import com.mailboom.api.infrastructure.dto.UpdateContactRequest;
import com.mailboom.api.infrastructure.persistence.jpa.entity.TokenEntity;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataUserRepository;
import com.mailboom.api.infrastructure.persistence.jpa.repository.TokenRepository;
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

import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContactControllerIT {

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
    private ContactRepository contactRepository;
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

        // JWT Properties for testing
        registry.add("application.security.jwt.secret-key", () -> "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        registry.add("application.security.jwt.expiration", () -> 86400000);
        registry.add("application.security.jwt.refresh-token.expiration", () -> 604800000);
    }

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        springDataUserRepository.deleteAll();

        // Crear un usuario de prueba
        testUser = User.create(
                UserId.generate(),
                new Email("pepito@hotmail.com"),
                new Name("Pepito"),
                new PasswordHash("8347364ufmns"),
                new EmailCounter(20)
        );
        userRepository.save(testUser);

        springDataUserRepository.flush();

        UserEntity userEntity = springDataUserRepository.findByEmail("pepito@hotmail.com")
                .orElseThrow(() -> new RuntimeException("User not found after save."));

        // Generar token
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
        tokenRepository.flush();

        // Crear una lista de contactos para el usuario
        testList = ContactList.create(testUser.getId(), new Name("Test List"));
        contactListRepository.save(testList);
    }

    @Test
    void shouldCreateContactSuccessfully() throws Exception {
        NewContactRequest request = new NewContactRequest(testList.getId().value().toString(), "newcontact@example.com", "New Contact", new HashMap<>(), true);

        mockMvc.perform(post("/contacts/new")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newcontact@example.com"));
    }

    @Test
    void shouldCreateContactListSuccessfully() throws Exception {
        NewContactListRequest newContactListRequest = new NewContactListRequest("New List", UUID.randomUUID().toString());
        mockMvc.perform(post("/contacts/new/list")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContactListRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateContactSuccessfully() throws Exception {
        // Given
        Contact contact = Contact.create(
                ContactId.generate(),
                testList.getId(),
                new Email("update@example.com"),
                new Name("Original Name"),
                new HashMap<>(),
                true
        );
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest(
                contact.getId().value().toString(),
                testList.getId().value().toString(),
                "updated@example.com",
                "Updated Name",
                new HashMap<>(),
                false
        );

        // When & Then
        mockMvc.perform(put("/contacts/" + contact.getId().value() + "/update")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.subscribed").value(false));
    }

    @Test
    void shouldUpdateContactListSuccessfully() throws Exception {
        // Given
        ContactList contactList = ContactList.create(testUser.getId(), new Name("Original Name"));
        contactListRepository.save(contactList);

        UpdateContactListRequest request = new UpdateContactListRequest("Updated Name", UUID.randomUUID().toString());
        // When & Then
        mockMvc.perform(put("/contacts/" + contactList.getId().value() + "/list/update")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void shouldDeleteContactSuccessfully() throws Exception {
        // Given
        Contact contact = Contact.create(
                ContactId.generate(),
                testList.getId(),
                new Email("delete@example.com"),
                new Name("Delete Me"),
                new HashMap<>(),
                true
        );
        contactRepository.save(contact);

        // When & Then
        mockMvc.perform(delete("/contacts/" + contact.getId().value() + "/delete")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteContactListSuccessfully() throws Exception {
        // Given
        ContactList contactList = ContactList.create(testUser.getId(), new Name("Delete Me"));
        contactListRepository.save(contactList);

        // When & Then
        mockMvc.perform(delete("/contacts/" + contactList.getId().value() + "/list/delete")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldGetContactSuccessfully() throws Exception {
        // Given
        Contact contact = Contact.create(
                ContactId.generate(),
                testList.getId(),
                new Email("get@example.com"),
                new Name("Get Me"),
                new HashMap<>(),
                true
        );
        contactRepository.save(contact);

        // When & Then
        mockMvc.perform(get("/contacts/" + contact.getId().value())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("get@example.com"))
                .andExpect(jsonPath("$.name").value("Get Me"));
    }

    @Test
    void shouldGetContactListSuccessfully() throws Exception {
        // Given
        ContactList contactList = ContactList.create(testUser.getId(), new Name("Get Me"));
        contactListRepository.save(contactList);
        // When & Then
        mockMvc.perform(get("/contacts/" + contactList.getId().value() + "/list")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Get Me"));
    }

    @Test
    void shouldGetContactListsFromOwnerSuccessfully() throws Exception {
        // Given
        mockMvc.perform(get("/contacts/list/user/" + testUser.getId().value())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}
