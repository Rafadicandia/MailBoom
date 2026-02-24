package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListIdNotFoundException;
import com.mailboom.api.application.contact.usecase.command.GetContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetContactListUseCaseImplTest {
    @InjectMocks
    private GetContactListUseCaseImpl getContactListUseCase;

    @Mock
    private ContactListRepository contactListRepository;

    @Test
    void shouldGetContactList() {
        // Given
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new GetContactListCommand(contactListId.toString());
        var contactList = ContactList.reCreate(contactListId, userId, new Name("Test List"), 0);

        when(contactListRepository.findById(contactListId)).thenReturn(Optional.of(contactList));

        // When
        var result = getContactListUseCase.execute(command);

        // Then
        assertEquals(contactList, result);
    }

    @Test
    void shouldThrowExceptionWhenContactListNotFound() {
        // Given
        var contactListId = ContactListId.generate();
        var command = new GetContactListCommand(contactListId.toString());

        when(contactListRepository.findById(contactListId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ContactListIdNotFoundException.class, () -> getContactListUseCase.execute(command));
    }
}
