package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteContactUseCaseImplTest {
    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactListRepository contactListRepository;

    private DeleteContactUseCaseImpl deleteContactUseCase;

    @BeforeEach
    void setUp() {
        deleteContactUseCase = new DeleteContactUseCaseImpl(contactRepository, contactListRepository);
    }

    @Test
    void shouldDeleteContact() {
        var contactId = ContactId.generate();
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        var command = new DeleteContactCommand(contactId.toString());

        var contactList = ContactList.reCreate(contactListId, userId, new Name("Test List"), 10);
        var contact = Contact.create(contactId, contactListId, new Email("test@test.com"), new Name("Test"), null, true);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactListRepository.findById(contactListId)).thenReturn(Optional.of(contactList));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of());

        deleteContactUseCase.execute(command);

        verify(contactRepository, times(1)).deleteById(contactId);
    }

    @Test
    void shouldThrowExceptionWhenContactNotFound() {
        var contactId = ContactId.generate();
        var command = new DeleteContactCommand(contactId.toString());

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> deleteContactUseCase.execute(command));
    }
}
