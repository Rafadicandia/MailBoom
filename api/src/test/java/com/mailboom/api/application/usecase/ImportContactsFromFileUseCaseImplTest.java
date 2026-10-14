package com.mailboom.api.application.usecase;

import com.mailboom.api.application.contact.usecase.ImportContactsFromFileUseCaseImpl;
import com.mailboom.api.application.contact.usecase.command.ImportContactsFromFileCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.FileParserFactory;
import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportContactsFromFileUseCaseImplTest {

    @Mock
    private ContactListRepository listRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private FileParserFactory parserFactory;
    @Mock
    private ContactFileParser parser;

    @InjectMocks
    private ImportContactsFromFileUseCaseImpl useCase;

    private ContactListId listId;
    private UserId ownerId;
    private InputStream inputStream;
    private String contentType;
    private ContactList existingList;

    @BeforeEach
    void setUp() {
        listId = new ContactListId(UUID.randomUUID());
        ownerId = new UserId(UUID.randomUUID());
        inputStream = new ByteArrayInputStream("test data".getBytes());
        contentType = "text/csv";
        
        existingList = ContactList.reCreate(listId, ownerId, new Name("Existing List"), 0);
    }

    @Test
    void shouldImportContactsSuccessfully() {
        // Given
        when(listRepository.findById(listId)).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            consumer.accept(new ContactData("test1@example.com", "Test 1", "+1234567890", new HashMap<>()));
            consumer.accept(new ContactData("test2@example.com", "Test 2", "+1234567891", new HashMap<>()));
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        useCase.execute(command);

        // Then
        ArgumentCaptor<List<Contact>> captor = ArgumentCaptor.forClass(List.class);
        verify(contactRepository).saveAll(captor.capture());
        
        List<Contact> savedContacts = captor.getValue();
        assertEquals(2, savedContacts.size());
        assertEquals("test1@example.com", savedContacts.get(0).getEmail().email());
        assertEquals("test2@example.com", savedContacts.get(1).getEmail().email());
    }

    @Test
    void shouldProcessContactsInBatches() {
        // Given
        when(listRepository.findById(listId)).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            // Simulate 1005 contacts to trigger batch processing (1000 + 5)
            for (int i = 0; i < 1005; i++) {
                consumer.accept(new ContactData("user" + i + "@example.com", "User " + i, "+1234567890", new HashMap<>()));
            }
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        useCase.execute(command);

        // Then
        // Should be called twice: once for the first 1000, once for the remaining 5
        verify(contactRepository, times(2)).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenListNotFound() {
        // Given
        when(listRepository.findById(listId)).thenReturn(Optional.empty());

        // When & Then
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(command);
        });
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotMatch() {
        // Given
        UserId anotherOwner = new UserId(UUID.randomUUID());
        ContactList listWithDifferentOwner = ContactList.reCreate(listId, anotherOwner, new Name("Another List"), 0);
        when(listRepository.findById(listId)).thenReturn(Optional.of(listWithDifferentOwner));

        // When & Then
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(command);
        });
    }

    @Test
    void shouldImportContactsWithEmptyEmail() {
        // Given
        when(listRepository.findById(listId)).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            consumer.accept(new ContactData("", "John Doe", "+1234567890", new HashMap<>()));
            consumer.accept(new ContactData("jane@example.com", "Jane Doe", "", new HashMap<>()));
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        useCase.execute(command);

        // Then
        ArgumentCaptor<List<Contact>> captor = ArgumentCaptor.forClass(List.class);
        verify(contactRepository).saveAll(captor.capture());
        
        List<Contact> savedContacts = captor.getValue();
        assertEquals(2, savedContacts.size());
        assertEquals("", savedContacts.get(0).getEmail().email());
        assertEquals("John Doe", savedContacts.get(0).getName().toString());
        assertEquals("jane@example.com", savedContacts.get(1).getEmail().email());
        assertEquals("", savedContacts.get(1).getPhone().toString());
    }

    @Test
    void shouldImportContactsWithInvalidEmailFormat() {
        // Given
        when(listRepository.findById(listId)).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            consumer.accept(new ContactData("valid@example.com", "Valid User", "+1234567890", new HashMap<>()));
            // Parser sets invalid emails to null, so we mock that behavior
            consumer.accept(new ContactData(null, "Invalid User", "+1234567891", new HashMap<>()));
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        ImportContactsFromFileCommand command = new ImportContactsFromFileCommand(
                listId.value().toString(),
                ownerId.value().toString(),
                inputStream,
                contentType
        );
        useCase.execute(command);

        // Then
        ArgumentCaptor<List<Contact>> captor = ArgumentCaptor.forClass(List.class);
        verify(contactRepository).saveAll(captor.capture());
        
        List<Contact> savedContacts = captor.getValue();
        assertEquals(2, savedContacts.size());
        assertEquals("valid@example.com", savedContacts.get(0).getEmail().email());
        // Invalid email should be set to empty string, not skipped
        Email invalidEmail = savedContacts.get(1).getEmail();
        assertTrue(invalidEmail == null || invalidEmail.email().isEmpty(),
            "Invalid email should be null or empty, but was: " + (invalidEmail != null ? invalidEmail.email() : "null"));
        assertEquals("Invalid User", savedContacts.get(1).getName().toString());
    }
}
