package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListAlreadyExistException;
import com.mailboom.api.application.contact.port.in.CreateContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateContactListUseCaseImpl implements CreateContactListUseCase {

    private final ContactListRepository contactListRepository;

    @Override
    @Transactional
    public ContactList execute(CreateContactListCommand command) {
        String normalizedCommandName = command.name().trim().toLowerCase();
        
        if (contactListRepository.findAllByUserId(command.ownerId()).stream().anyMatch(
                contactList -> contactList.getName().value().trim().toLowerCase().equals(normalizedCommandName))) {
            throw new ContactListAlreadyExistException("Contact list with name " + command.name() + " already exists");
        }

        return contactListRepository.save(
                ContactList.create(
                        new UserId(command.ownerId()),
                        new Name(command.name())
                )
        );
    }
}
