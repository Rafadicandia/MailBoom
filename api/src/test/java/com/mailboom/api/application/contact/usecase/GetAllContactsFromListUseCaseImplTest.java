package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.GetAllContactsFromListCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.port.out.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllContactsFromListUseCaseImplTest {
    @InjectMocks
    private GetAllContactsFromListUseCaseImpl getAllContactsFromListUseCase;

    @Mock
    private ContactRepository contactRepository;

    @Test
    void shouldGetAllContactsFromList() {
        // Given
        var contactListId = ContactListId.generate();
        var command = new GetAllContactsFromListCommand(contactListId.toString());
        var contact = Contact.create(ContactId.generate(), contactListId, new Email("test@test.com"), new Name("Test"), null, true);

        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(List.of(contact));

        // When
        var result = getAllContactsFromListUseCase.execute(command);

        // Then
        assertEquals(List.of(contact), result);
    }
}
