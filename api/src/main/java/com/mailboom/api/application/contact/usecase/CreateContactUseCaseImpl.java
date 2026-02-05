package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.CreateContactUseCase;
import com.mailboom.api.application.contact.usecase.command.CreateContactCommand;
import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Email;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateContactUseCaseImpl implements CreateContactUseCase {
    private final ContactRepository contactRepository;

    @Override
    public Contact execute(CreateContactCommand command) {
        if (contactRepository.findByEmail(new Email(command.email())).isPresent()) {
            throw new IllegalArgumentException("Contact with email " + command.email() + " already exists");
        }
        Contact newContact = Contact.create(
                ContactId.generate(),
                ContactListId.fromString(command.contactListId()),
                new Email(command.email()),
                new Name(command.name()),
                command.customFields(),
                command.subscribed()
        );
        return contactRepository.save(newContact);
    }
}
