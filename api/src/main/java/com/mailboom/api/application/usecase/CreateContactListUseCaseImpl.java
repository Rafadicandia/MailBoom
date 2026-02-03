package com.mailboom.api.application.usecase;

import com.mailboom.api.application.exception.ContactListAlreadyExistException;
import com.mailboom.api.application.port.in.CreateContactListUseCase;
import com.mailboom.api.application.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.ContactList;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.model.valueobjects.UserId;
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
        if (contactListRepository.findAllByUserId(command.ownerId()).stream().anyMatch(
                contactList -> contactList.getName().toString().trim().toLowerCase().equals(command.name()))) {
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
