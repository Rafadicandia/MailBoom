package com.mailboom.api.application.usecase;

import com.mailboom.api.application.common.exception.ContactListAlreadyExistException;
import com.mailboom.api.application.contact.usecase.CreateContactListUseCaseImpl;
import com.mailboom.api.application.contact.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateContactListUseCaseImplTest {

    @Mock
    private ContactListRepository contactListRepository;

    @InjectMocks
    private CreateContactListUseCaseImpl createContactListUseCase;

    private CreateContactListCommand command;
    private UserId ownerId;

    @BeforeEach
    void setUp() {
        ownerId = new UserId(UUID.randomUUID());
        command = new CreateContactListCommand("Test List", ownerId.value());
    }

    @Test
    void shouldCreateContactListSuccessfully() {
        // Given
        when(contactListRepository.findAllByUserId(new UserId(ownerId.value()))).thenReturn(Collections.emptyList());
        when(contactListRepository.save(any(ContactList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ContactList result = createContactListUseCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals("Test List", result.getName().value());
        assertEquals(ownerId, result.getOwner());
    }

    @Test
    void shouldThrowExceptionWhenContactListAlreadyExists() {
        // Given
        ContactList existingList = ContactList.create(ownerId, new Name("Test List"));
        when(contactListRepository.findAllByUserId(new UserId(ownerId.value()))).thenReturn(Collections.singletonList(existingList));

        // When & Then
        assertThrows(ContactListAlreadyExistException.class, () -> {
            createContactListUseCase.execute(command);
        });
    }
}
