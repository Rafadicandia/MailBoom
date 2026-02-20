package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvContactFileParserTest {

    private final CsvContactFileParser parser = new CsvContactFileParser();

    @Test
    void shouldParseCsvFileCorrectly() throws IOException {
        ClassPathResource resource = new ClassPathResource("csvExample/contacts.csv");
        try (InputStream inputStream = resource.getInputStream()) {
            List<ContactData> contacts = new ArrayList<>();
            parser.parse(inputStream, contacts::add);

            assertFalse(contacts.isEmpty(), "Contacts list should not be empty");
            assertEquals(20, contacts.size(), "Should parse 20 contacts");

            ContactData firstContact = contacts.get(0);
            assertEquals("juan.perez@example.com", firstContact.email());
            assertEquals("Juan Perez", firstContact.name()); // Should have name from file

            ContactData lastContact = contacts.get(contacts.size() - 1);
            assertEquals("paula.j@redes.net", lastContact.email());
            assertEquals("Paula Jimenez", lastContact.name()); // Should have name from file
        }
    }

    @Test
    void shouldAssignDefaultNameWhenNameIsEmpty() {
        String csvContent = "email,nombre\nuser@example.com,"; // Empty name
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(1, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertEquals("User", contacts.get(0).name()); // Default name
    }

    @Test
    void shouldAssignDefaultNameWhenNameColumnIsMissing() {
        String csvContent = "email,phone\nuser@example.com,123456"; // No name column
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(1, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertEquals("User", contacts.get(0).name()); // Default name
    }

    @Test
    void shouldThrowExceptionWhenEmailColumnMissing() {
        String csvContent = "nombre,telefono\nJuan,123456";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        assertThrows(RuntimeException.class, () -> parser.parse(inputStream, contact -> {}));
    }
}
