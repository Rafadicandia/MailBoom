package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CsvContactFileParserQuotesTest {

    private final CsvContactFileParser parser = new CsvContactFileParser();

    @Test
    void shouldParseContactosPedidosCsvFileFromResources() throws IOException {
        ClassPathResource resource = new ClassPathResource("csvExample/CONTACTOS_PEDIDOS.csv");
        try (InputStream inputStream = resource.getInputStream()) {
            List<ContactData> contacts = new ArrayList<>();
            parser.parse(inputStream, contacts::add);

            assertFalse(contacts.isEmpty(), "Contacts list should not be empty");
            assertEquals(9, contacts.size(), "Should parse 9 contacts (including 2 with empty email)");

            // Verify first contact
            ContactData firstContact = contacts.get(0);
            assertEquals("lizavellino126@gmail.com", firstContact.email());
            assertEquals("Liz", firstContact.name());
            assertEquals("+59894588066", firstContact.phone());
            assertEquals("Chacra", firstContact.attributes().get("tipo"));
            assertEquals("CANELONES", firstContact.attributes().get("zonas"));

            // Verify contact with quoted tipo containing commas
            ContactData thirdContact = contacts.get(2);
            assertEquals("anadafirey@gmail.com", thirdContact.email());
            assertEquals("Terreno, Chacra", thirdContact.attributes().get("tipo"));
            assertEquals("Peñarol", thirdContact.attributes().get("zonas"));

            // Find contact with email inmobiliaria@arteagahill.com (order may vary)
            Optional<ContactData> inmobiliariaContact = contacts.stream()
                    .filter(c -> "inmobiliaria@arteagahill.com".equals(c.email()))
                    .findFirst();
            assertTrue(inmobiliariaContact.isPresent(), "Should find contact with email inmobiliaria@arteagahill.com");
            assertEquals("Casa, Terreno, Chacra", inmobiliariaContact.get().attributes().get("tipo"));
            assertEquals("Peñarol, Melilla, Pajas, Blancas", inmobiliariaContact.get().attributes().get("zonas"));

            // Find contact with email alevir@adinet.com.uy (order may vary)
            Optional<ContactData> alevirContact = contacts.stream()
                    .filter(c -> "alevir@adinet.com.uy".equals(c.email()))
                    .findFirst();
            assertTrue(alevirContact.isPresent(), "Should find contact with email alevir@adinet.com.uy");
            assertEquals("hector alejandro", alevirContact.get().name());
            assertEquals("+59899682886", alevirContact.get().phone());
            assertEquals("Chacra", alevirContact.get().attributes().get("tipo"));
            assertEquals("Los, Cerrillos", alevirContact.get().attributes().get("zonas"));

            // Verify contacts with empty email
            long emptyEmailCount = contacts.stream().filter(c -> c.email() == null || c.email().isEmpty()).count();
            assertEquals(2, emptyEmailCount, "Should have 2 contacts with empty email");
        }
    }

    @Test
    void shouldHandleQuotedFieldsWithCommas() {
        String csvContent = "email,nombre,tipo,zonas,telefono\n" +
                "\"user@example.com\",\"Juan Perez\",\"Casa, Terreno\",\"Peñarol, Melilla\",\"+59812345678\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(1, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertEquals("Juan Perez", contacts.get(0).name());
        assertEquals("Casa, Terreno", contacts.get(0).attributes().get("tipo"));
        assertEquals("Peñarol, Melilla", contacts.get(0).attributes().get("zonas"));
        assertEquals("+59812345678", contacts.get(0).phone());
    }

    @Test
    void shouldHandleQuotedFieldsWithNewlines() {
        String csvContent = "email,nombre,tipo,zonas,telefono\n" +
                "\"user@example.com\",\"Juan Perez\",\"Casa\",\"Peñarol\",\"+59812345678\"\n" +
                "\"user2@example.com\",\"Maria Garcia\",\"Apartamento\",\"Centro\",\"+59887654321\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(2, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertEquals("user2@example.com", contacts.get(1).email());
    }

    @Test
    void shouldHandleEmptyQuotedFields() {
        String csvContent = "email,nombre,tipo,zonas,telefono\n" +
                "\"user@example.com\",\"Juan Perez\",\"\",\"Peñarol\",\"+59812345678\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(1, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertNull(contacts.get(0).attributes().get("tipo"), "Empty quoted field should be null");
        assertEquals("Peñarol", contacts.get(0).attributes().get("zonas"));
    }

    @Test
    void shouldHandleMixedQuotedAndUnquotedFields() {
        String csvContent = "email,nombre,tipo,zonas,telefono\n" +
                "user@example.com,\"Juan Perez\",Casa,Peñarol,+59812345678";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<ContactData> contacts = new ArrayList<>();

        parser.parse(inputStream, contacts::add);

        assertEquals(1, contacts.size());
        assertEquals("user@example.com", contacts.get(0).email());
        assertEquals("Juan Perez", contacts.get(0).name());
        assertEquals("Casa", contacts.get(0).attributes().get("tipo"));
        assertEquals("Peñarol", contacts.get(0).attributes().get("zonas"));
        assertEquals("+59812345678", contacts.get(0).phone());
    }
}
