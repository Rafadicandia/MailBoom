package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.CreateContactUseCase;
import com.mailboom.api.application.contact.usecase.command.CreateContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CreateContactUseCaseImpl implements CreateContactUseCase {
    private final ContactRepository contactRepository;
    private final ContactListRepository contactListRepository;

    @Override
    public Contact execute(CreateContactCommand command) {
        if (command.contactListId() == null) {
            throw new IllegalArgumentException("Contact list ID cannot be null");
        }

        ContactListId contactListId = ContactListId.fromString(command.contactListId());
        ContactList contactList = contactListRepository.findById(contactListId).orElseThrow();
        List<Contact> totalContacts = contactRepository.findAllByContactListId(contactListId);

        Contact newContact = Contact.create(
                ContactId.generate(),
                contactListId,
                new Email(command.email()),
                new Name(command.name()),
                command.customFields(),
                command.subscribed()
        );
        ContactList updatedContactList = contactList.updateTotalContacts(totalContacts.size() + 1);
        contactListRepository.save(updatedContactList);
        return contactRepository.save(newContact);


    }
}
