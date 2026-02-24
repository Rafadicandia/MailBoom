package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.GetContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.port.out.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetContactUseCaseImplTest {
    @Mock
    private ContactRepository contactRepository;

    private GetContactUseCaseImpl getContactUseCase;

    @BeforeEach
    void setUp() {
        getContactUseCase = new GetContactUseCaseImpl(contactRepository);
    }

    @Test
    void shouldGetContact() {
        var contactId = ContactId.generate();
        var contactListId = ContactListId.generate();
        var command = new GetContactCommand(contactId.toString());
        var contact = Contact.create(contactId, contactListId, new Email("test@test.com"), new Name("Test"), null, true);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        var result = getContactUseCase.execute(command);

        assertEquals(contact, result);
    }

    @Test
    void shouldThrowExceptionWhenContactNotFound() {
        var contactId = ContactId.generate();
        var command = new GetContactCommand(contactId.toString());

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> getContactUseCase.execute(command));
    }
}
