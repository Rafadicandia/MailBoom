package com.mailboom.api.infrastructure.controller;

import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.domain.model.user.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContactImportE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.mailboom.api.domain.port.out.UserRepository userRepository;

    @Autowired
    private com.mailboom.api.domain.port.out.ContactListRepository contactListRepository;

    @Autowired
    private com.mailboom.api.domain.port.out.ContactRepository contactRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Autowired
    private com.mailboom.api.infrastructure.security.JwtService jwtService;

    private User testUser;
    private ContactList testList;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create test user
        Set<ContactListId> emptyContactLists = new HashSet<>();
        testUser = User.create(
                UserId.generate(),
                new Email("pepito@hotmail.com"),
                new Name("Pepito Test"),
                new PasswordHash(passwordEncoder.encode("password123")),
                PlanType.FREE,
                EmailCounter.zero(),
                Role.USER,
                emptyContactLists
        );
        userRepository.save(testUser);

        // Create test contact list
        testList = ContactList.create(testUser.getId(), new Name("Test Contact List"));
        contactListRepository.save(testList);

        // Generate JWT token
        var userDetails = userDetailsService.loadUserByUsername(testUser.getEmail().email());
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    void shouldImportContactsFromContactosPedidosCsvFile() throws Exception {
        // Given - Load the actual CONTACTOS_PEDIDOS.csv file from resources
        org.springframework.core.io.ClassPathResource resource = new org.springframework.core.io.ClassPathResource("csvExample/CONTACTOS_PEDIDOS.csv");
        byte[] csvBytes = org.springframework.util.StreamUtils.copyToByteArray(resource.getInputStream());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "CONTACTOS_PEDIDOS.csv",
                "text/csv",
                csvBytes
        );

        // When - Import the CSV file via the REST API
        mockMvc.perform(multipart("/api/contacts/import/file")
                        .file(file)
                        .param("listId", testList.getId().value().toString())
                        .param("ownerId", testUser.getId().value().toString())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));

        // Then - Verify all contacts are correctly persisted in the database
        List<Contact> persistedContacts = contactRepository.findAllByContactListId(testList.getId());
        assertEquals(9, persistedContacts.size(), "Should have 9 contacts persisted in database");

        // Find contacts by email instead of using fixed indices
        Optional<Contact> lizContact = findContactByEmail(persistedContacts, "lizavellino126@gmail.com");
        assertTrue(lizContact.isPresent(), "Should find Liz's contact");
        assertEquals("Liz", lizContact.get().getName().toString());
        assertEquals("+59894588066", lizContact.get().getPhone().toString());
        assertEquals("Chacra", lizContact.get().getCustomFields().get("tipo"));
        assertEquals("CANELONES", lizContact.get().getCustomFields().get("zonas"));

        Optional<Contact> hugoContact = findContactByEmail(persistedContacts, "silvanadelgado9@hotmail.com");
        assertTrue(hugoContact.isPresent(), "Should find Hugo's contact");
        assertEquals("Hugo", hugoContact.get().getName().toString());
        assertEquals("+59895886421", hugoContact.get().getPhone().toString());
        assertEquals("Chacra", hugoContact.get().getCustomFields().get("tipo"));
        assertFalse(hugoContact.get().getCustomFields().containsKey("zonas"));

        Optional<Contact> anaContact = findContactByEmail(persistedContacts, "anadafirey@gmail.com");
        assertTrue(anaContact.isPresent(), "Should find Ana's contact");
        assertEquals("Ana Luisa", anaContact.get().getName().toString());
        assertEquals("+59899596193", anaContact.get().getPhone().toString());
        assertEquals("Terreno, Chacra", anaContact.get().getCustomFields().get("tipo"));
        assertEquals("Peñarol", anaContact.get().getCustomFields().get("zonas"));

        Optional<Contact> karenContact = findContactByEmail(persistedContacts, "karencerro15k@gmail.com");
        assertTrue(karenContact.isPresent(), "Should find Karen's contact");
        assertEquals("Karen", karenContact.get().getName().toString());
        assertEquals("+59898068164", karenContact.get().getPhone().toString());
        assertEquals("Campo, Terreno, Chacra", karenContact.get().getCustomFields().get("tipo"));
        assertFalse(karenContact.get().getCustomFields().containsKey("zonas"));

        Optional<Contact> gabrielContact = findContactByEmail(persistedContacts, "inmobiliaria@arteagahill.com");
        assertTrue(gabrielContact.isPresent(), "Should find Gabriel's contact");
        assertEquals("Gabriel Colega Inm. Arteaga Hill", gabrielContact.get().getName().toString());
        assertEquals("+59892637474", gabrielContact.get().getPhone().toString());
        assertEquals("Casa, Terreno, Chacra", gabrielContact.get().getCustomFields().get("tipo"));
        assertEquals("Peñarol, Melilla, Pajas, Blancas", gabrielContact.get().getCustomFields().get("zonas"));

        Optional<Contact> enriqueContact = findContactByEmail(persistedContacts, "hbfb2009@hotmail.com");
        assertTrue(enriqueContact.isPresent(), "Should find Enrique's contact");
        assertEquals("Enrique", enriqueContact.get().getName().toString());
        assertEquals("+59899468079", enriqueContact.get().getPhone().toString());
        assertEquals("Apartamento", enriqueContact.get().getCustomFields().get("tipo"));
        assertEquals("Ciudad, Vieja, Centro", enriqueContact.get().getCustomFields().get("zonas"));

        Optional<Contact> hectorContact = findContactByEmail(persistedContacts, "alevir@adinet.com.uy");
        assertTrue(hectorContact.isPresent(), "Should find Hector's contact");
        assertEquals("hector alejandro", hectorContact.get().getName().toString());
        assertEquals("+59899682886", hectorContact.get().getPhone().toString());
        assertEquals("Chacra", hectorContact.get().getCustomFields().get("tipo"));
        assertEquals("Los, Cerrillos", hectorContact.get().getCustomFields().get("zonas"));

        // Verify contacts with empty email
        long emptyEmailCount = persistedContacts.stream()
                .filter(c -> c.getEmail() == null || c.getEmail().toString().isEmpty())
                .count();
        assertEquals(2, emptyEmailCount, "Should have 2 contacts with empty email");
    }

    @Test
    void shouldGetImportedContactsFromList() throws Exception {
        // Given - First import the CSV file
        org.springframework.core.io.ClassPathResource resource = new org.springframework.core.io.ClassPathResource("csvExample/CONTACTOS_PEDIDOS.csv");
        byte[] csvBytes = org.springframework.util.StreamUtils.copyToByteArray(resource.getInputStream());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "CONTACTOS_PEDIDOS.csv",
                "text/csv",
                csvBytes
        );

        mockMvc.perform(multipart("/api/contacts/import/file")
                        .file(file)
                        .param("listId", testList.getId().value().toString())
                        .param("ownerId", testUser.getId().value().toString())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        // When & Then - Get contacts from the list via REST API
        mockMvc.perform(get("/api/contacts/list/" + testList.getId().value() + "/contacts")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(9)))
                .andExpect(jsonPath("$.content[0].email").value("lizavellino126@gmail.com"))
                .andExpect(jsonPath("$.content[0].name").value("Liz"))
                .andExpect(jsonPath("$.content[0].phone").value("+59894588066"))
                .andExpect(jsonPath("$.content[0].customFields.tipo").value("Chacra"))
                .andExpect(jsonPath("$.content[0].customFields.zonas").value("CANELONES"))
                .andExpect(jsonPath("$.content[6].email").value("alevir@adinet.com.uy"))
                .andExpect(jsonPath("$.content[6].name").value("hector alejandro"))
                .andExpect(jsonPath("$.content[6].phone").value("+59899682886"))
                .andExpect(jsonPath("$.content[6].customFields.tipo").value("Chacra"))
                .andExpect(jsonPath("$.content[6].customFields.zonas").value("Los, Cerrillos"))
                .andExpect(jsonPath("$.content[7].email").value(""))
                .andExpect(jsonPath("$.content[8].email").value(""));
    }

    private Optional<Contact> findContactByEmail(List<Contact> contacts, String email) {
        return contacts.stream()
                .filter(c -> c.getEmail() != null && email.equals(c.getEmail().email()))
                .findFirst();
    }
}
