package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.DeleteContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.DeleteContactListCommand;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteContactListUseCaseImpl implements DeleteContactListUseCase {

    private final ContactListRepository contactListRepository;

    @Override
    public void execute(DeleteContactListCommand command) {
        if (command.id() == null || command.id().isBlank()) {
            throw new IllegalArgumentException("Contact List ID is required");
        }
        contactListRepository.deleteById(ContactListId.fromString(command.id()));

    }
}
