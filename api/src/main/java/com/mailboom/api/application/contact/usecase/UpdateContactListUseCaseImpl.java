package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.common.exception.ContactListIdNotFoundException;
import com.mailboom.api.application.contact.port.in.UpdateContactListUseCase;
import com.mailboom.api.application.contact.usecase.command.UpdateContactListCommand;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.port.out.ContactListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateContactListUseCaseImpl implements UpdateContactListUseCase {
    private final ContactListRepository contactListRepository;

    @Override
    public ContactList execute(UpdateContactListCommand command) {
        if(contactListRepository.findById(UUID.fromString(command.id())).isEmpty()){
            throw new ContactListIdNotFoundException("ContactList not found with id: " + command.id());

        }
        Optional<ContactList> contactList = contactListRepository.findById(UUID.fromString(command.id()));
        if (contactList.isPresent()) {
            ContactList updatedContactList = contactList.get().updateName(new Name(command.name()));
            return contactListRepository.save(updatedContactList);
        }





        return null;
    }
}
