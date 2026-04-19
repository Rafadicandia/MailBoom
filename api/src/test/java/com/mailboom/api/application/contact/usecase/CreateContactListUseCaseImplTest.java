package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListAlreadyExistException;
import com.mailboom.api.application.contact.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateContactListUseCaseImplTest {
    @InjectMocks
    private CreateContactListUseCaseImpl createContactListUseCase;

    @Mock
    private ContactListRepository contactListRepository;

    @Test
    void shouldCreateContactList() {
        // Given
        var userId = UUID.randomUUID();
        var userIdValueObject = new UserId(userId);
        var command = new CreateContactListCommand("New List", userId);
        var contactList = ContactList.create(userIdValueObject, new Name("New List"));

        when(contactListRepository.findAllByUserId(userIdValueObject)).thenReturn(Collections.emptyList());
        when(contactListRepository.save(any(ContactList.class))).thenReturn(contactList);

        // When
        var result = createContactListUseCase.execute(command);

        // Then
        assertEquals(contactList, result);
    }

    @Test
    void shouldThrowExceptionWhenContactListAlreadyExists() {
        // Given
        var userId = UUID.randomUUID();
        var userIdValueObject = new UserId(userId);
        var command = new CreateContactListCommand("Existing List", userId);
        var existingList = ContactList.create(userIdValueObject, new Name("Existing List"));

        when(contactListRepository.findAllByUserId(userIdValueObject)).thenReturn(List.of(existingList));

        // When & Then
        assertThrows(ContactListAlreadyExistException.class, () -> createContactListUseCase.execute(command));
    }
}
