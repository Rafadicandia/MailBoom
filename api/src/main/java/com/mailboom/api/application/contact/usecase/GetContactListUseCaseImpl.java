package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListIdNotFoundException;
import com.mailboom.api.application.contact.port.in.GetContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.GetContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class GetContactListUseCaseImpl implements GetContactListUseCase {
    private final ContactListRepository contactListRepository;

    @Override
    public ContactList execute(GetContactListCommand command) {
        if (command.id() == null || command.id().isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        return contactListRepository.findById(new ContactListId(UUID.fromString(command.id())))
                .orElseThrow(() -> new ContactListIdNotFoundException("Contact list not found"));
    }
}
