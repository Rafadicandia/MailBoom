package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.usecase.command.CreateContactCommand;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateContactUseCaseImplTest {
    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactListRepository contactListRepository;

    private CreateContactUseCaseImpl createContactUseCase;

    @BeforeEach
    void setUp() {
        createContactUseCase = new CreateContactUseCaseImpl(contactRepository, contactListRepository);
    }

    @Test
    void shouldCreateContactAndIncrementContactListTotal() {
        var contactListId = ContactListId.generate();
        var userId = UserId.generate();
        
        // Create contact list with 5 existing contacts
        var existingContactList = ContactList.reCreate(contactListId, userId, new Name("Test List"), 5);
        
        var command = new CreateContactCommand(
                contactListId.toString(),
                "test@test.com",
                "Test",
                null,
                true
        );

        var newContact = Contact.create(ContactId.generate(), contactListId, new Email(command.email()), new Name(command.name()), command.customFields(), command.subscribed());

        // Mock findAllByContactListId to return 5 existing contacts
        List<Contact> existingContacts = List.of(
            Contact.create(ContactId.generate(), contactListId, new Email("test1@test.com"), new Name("Test1"), null, true),
            Contact.create(ContactId.generate(), contactListId, new Email("test2@test.com"), new Name("Test2"), null, true),
            Contact.create(ContactId.generate(), contactListId, new Email("test3@test.com"), new Name("Test3"), null, true),
            Contact.create(ContactId.generate(), contactListId, new Email("test4@test.com"), new Name("Test4"), null, true),
            Contact.create(ContactId.generate(), contactListId, new Email("test5@test.com"), new Name("Test5"), null, true)
        );

        when(contactListRepository.findById(contactListId)).thenReturn(Optional.of(existingContactList));
        when(contactRepository.findAllByContactListId(contactListId)).thenReturn(existingContacts);
        when(contactRepository.save(any(Contact.class))).thenReturn(newContact);
        
        var result = createContactUseCase.execute(command);

        assertEquals(newContact, result);
        
        // Verify that contactListRepository.save is called with the updated contact list
        ArgumentCaptor<ContactList> contactListCaptor = ArgumentCaptor.forClass(ContactList.class);
        verify(contactListRepository).save(contactListCaptor.capture());
        
        // Verify the totalContacts is incremented (5 existing + 1 new = 6)
        ContactList savedContactList = contactListCaptor.getValue();
        assertEquals(6, savedContactList.getTotalContacts());
    }
}
