package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.DeleteContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.DeleteContactListCommand;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class DeleteContactListUseCaseImpl implements DeleteContactListUseCase {

    private final ContactListRepository contactListRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public void execute(DeleteContactListCommand command) {
        if (command.id() == null || command.id().isBlank()) {
            throw new IllegalArgumentException("Contact List ID is required");
        }
        ContactListId contactListId = ContactListId.fromString(command.id());

        contactRepository.findAllByContactListId(contactListId).forEach(contact ->
                contactRepository.deleteById(contact.getId())
        );

        contactListRepository.deleteById(contactListId);
    }
}
