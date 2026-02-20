package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListIdNotFoundException;
import com.mailboom.api.application.contact.port.in.UpdateContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.UpdateContactListCommand;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateContactListUseCaseImpl implements UpdateContactListUseCase {
    private final ContactListRepository contactListRepository;

    @Override
    public ContactList execute(UpdateContactListCommand command) {
        ContactListId contactListId = new ContactListId(UUID.fromString(command.id()));

        ContactList contactList = contactListRepository.findById(contactListId)
                .orElseThrow(() -> new ContactListIdNotFoundException("ContactList not found with id: " + command.id()));

        ContactList updatedContactList = contactList.updateName(new Name(command.name()));

        return contactListRepository.save(updatedContactList);
    }
}
