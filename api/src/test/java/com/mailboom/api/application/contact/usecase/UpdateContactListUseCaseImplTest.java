package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListIdNotFoundException;
import com.mailboom.api.application.contact.usecase.command.UpdateContactListCommand;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateContactListUseCaseImplTest {
    @InjectMocks
    private UpdateContactListUseCaseImpl updateContactListUseCase;

    @Mock
    private ContactListRepository contactListRepository;

    @Test
    void shouldUpdateContactList() {
        // Given
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new UpdateContactListCommand(contactListId.toString(), "New Name", userId.toString());
        var existingContactList = ContactList.reCreate(contactListId, userId, new Name("Old Name"), 0);
        var updatedContactList = existingContactList.updateName(new Name("New Name"));

        when(contactListRepository.findById(contactListId)).thenReturn(Optional.of(existingContactList));
        when(contactListRepository.save(any(ContactList.class))).thenReturn(updatedContactList);

        var result = updateContactListUseCase.execute(command);

        // Then
        assertEquals(updatedContactList, result);
    }

    @Test
    void shouldThrowExceptionWhenContactListNotFound() {
        // Given
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new UpdateContactListCommand(contactListId.toString(), "New Name", userId.toString());

        when(contactListRepository.findById(contactListId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ContactListIdNotFoundException.class, () -> updateContactListUseCase.execute(command));
    }
}
