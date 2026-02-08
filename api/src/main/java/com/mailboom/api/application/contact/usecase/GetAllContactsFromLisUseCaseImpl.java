package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.GetAllContactsFromListUseCase;
import com.mailboom.api.application.contact.usecase.command.GetAllContactsFromListCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GetAllContactsFromLisUseCaseImpl implements GetAllContactsFromListUseCase {
    private final ContactRepository contactRepository;

    @Override
    public List<Contact> execute(GetAllContactsFromListCommand command) {
        if (command.listId() == null || command.listId().isBlank()) {
            throw new IllegalArgumentException("List ID is required");
        }
        ContactListId contactListId = new ContactListId(UUID.fromString(command.listId()));
        return contactRepository.findAllByContactListId(contactListId);
    }
}
