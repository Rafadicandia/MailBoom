package com.mailboom.api.application.usecase;

import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.ContactList;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.model.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.FileParserFactory;
import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.infrastructure.dto.ContactData;
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
        
        existingList = ContactList.create(ownerId, new Name("Existing List"));
    }

    @Test
    void shouldImportContactsSuccessfully() {
        // Given
        when(listRepository.findById(listId.value())).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            consumer.accept(new ContactData("test1@example.com", "Test 1", new HashMap<>()));
            consumer.accept(new ContactData("test2@example.com", "Test 2", new HashMap<>()));
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        useCase.execute(listId, ownerId, inputStream, contentType);

        // Then
        ArgumentCaptor<List<Contact>> captor = ArgumentCaptor.forClass(List.class);
        verify(contactRepository).saveAll(captor.capture());
        
        List<Contact> savedContacts = captor.getValue();
        assertEquals(2, savedContacts.size());
        assertEquals("test1@example.com", savedContacts.get(0).getEmail().toString());
        assertEquals("test2@example.com", savedContacts.get(1).getEmail().toString());
    }

    @Test
    void shouldProcessContactsInBatches() {
        // Given
        when(listRepository.findById(listId.value())).thenReturn(Optional.of(existingList));
        when(parserFactory.getParser(contentType)).thenReturn(parser);
        
        doAnswer(invocation -> {
            Consumer<ContactData> consumer = invocation.getArgument(1);
            // Simulate 1005 contacts to trigger batch processing (1000 + 5)
            for (int i = 0; i < 1005; i++) {
                consumer.accept(new ContactData("user" + i + "@example.com", "User " + i, new HashMap<>()));
            }
            return null;
        }).when(parser).parse(eq(inputStream), any());

        // When
        useCase.execute(listId, ownerId, inputStream, contentType);

        // Then
        // Should be called twice: once for the first 1000, once for the remaining 5
        verify(contactRepository, times(2)).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenListNotFound() {
        // Given
        when(listRepository.findById(listId.value())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(listId, ownerId, inputStream, contentType);
        });
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotMatch() {
        // Given
        UserId anotherOwner = new UserId(UUID.randomUUID());
        ContactList listWithDifferentOwner = ContactList.create(anotherOwner, new Name("Another List"));
        when(listRepository.findById(listId.value())).thenReturn(Optional.of(listWithDifferentOwner));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(listId, ownerId, inputStream, contentType);
        });
    }
}
