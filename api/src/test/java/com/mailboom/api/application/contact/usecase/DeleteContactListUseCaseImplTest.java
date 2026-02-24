package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.DeleteContactListCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteContactListUseCaseImplTest {
    @InjectMocks
    private DeleteContactListUseCaseImpl deleteContactListUseCase;

    @Mock
    private ContactListRepository contactListRepository;

    @Mock
    private ContactRepository contactRepository;

    @Test
    void shouldDeleteContactListAndAssociatedContacts() {
        // Given
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new DeleteContactListCommand(contactListId.toString());
        var contactList = ContactList.reCreate(contactListId, userId, new Name("Test List"), 1);
        var contact = Contact.create(ContactId.generate(), contactListId, new Email("test@test.com"), new Name("Test"), null, true);

        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(contact));

        // When
        deleteContactListUseCase.execute(command);

        // Then
        verify(contactRepository, times(1)).deleteById(contact.getId());
        verify(contactListRepository, times(1)).deleteById(contactListId);
    }

    @Test
    void shouldDeleteContactListWhenNoContactsAssociated() {
        // Given
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new DeleteContactListCommand(contactListId.toString());

        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(Collections.emptyList());

        // When
        deleteContactListUseCase.execute(command);

        // Then
        verify(contactRepository, never()).deleteById(any());
        verify(contactListRepository, times(1)).deleteById(contactListId);
    }
}
