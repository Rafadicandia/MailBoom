package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.UpdateContactCommand;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateContactUseCaseImplTest {
    @InjectMocks
    private UpdateContactUseCaseImpl updateContactUseCase;

    @Mock
    private ContactRepository contactRepository;

    @Test
    void shouldUpdateContact() {
        // Given
        var contactId = ContactId.generate();
        var contactListId = ContactListId.generate();
        var command = new UpdateContactCommand(contactId.toString(), "new@email.com", "New Name", null, true);
        var existingContact = Contact.create(contactId, contactListId, new Email("old@email.com"), new Name("Old Name"), null, true);
        var updatedContact = existingContact.updateContact(new Email(command.email()), new Name(command.name()), command.customFields(), command.subscribed());

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        // When
        var result = updateContactUseCase.execute(command);

        // Then
        assertEquals(updatedContact, result);
    }

    @Test
    void shouldThrowExceptionWhenContactNotFound() {
        // Given
        var contactId = ContactId.generate();
        var command = new UpdateContactCommand(contactId.toString(), "new@email.com", "New Name", null, true);

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> updateContactUseCase.execute(command));
    }
}
