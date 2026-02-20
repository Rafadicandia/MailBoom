package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.DeleteContactUseCase;
import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DeleteContactUseCaseImpl implements DeleteContactUseCase {
    private final ContactRepository contactRepository;
    private final ContactListRepository contactListRepository;


    @Override
    public void execute(DeleteContactCommand command) {
        Contact contact = contactRepository.findById(ContactId.fromString(command.id())).orElseThrow();
        ContactListId contactListId = ContactListId.fromString(contact.getListId().toString());
        ContactList contactList = contactListRepository.findById(contactListId).orElseThrow();

        if (contactRepository.findById(ContactId.fromString(command.id())).isEmpty()) {
            throw new IllegalArgumentException("Contact with id " + command.id() + " does not exist");
        }
        contactRepository.deleteById(ContactId.fromString(command.id()));
        System.out.println("Contact with id " + command.id() + " deleted successfully");
        List<Contact> totalContacts = contactRepository.findAllByContactListId(contactListId);
        ContactList updatedContactList = contactList.updateTotalContacts(totalContacts.size());
        contactListRepository.save(updatedContactList);
    }
}
