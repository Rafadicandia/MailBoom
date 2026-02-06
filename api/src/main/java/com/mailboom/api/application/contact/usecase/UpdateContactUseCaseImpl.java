package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.UpdateContactUseCase;
import com.mailboom.api.application.contact.usecase.command.UpdateContactCommand;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateContactUseCaseImpl implements UpdateContactUseCase {
    private final ContactRepository contactRepository;

    @Override
    public Contact execute(UpdateContactCommand command) {

        Contact contact = contactRepository.findById(
                new ContactId(UUID.fromString(command.id()))
        ).orElseThrow(() -> new IllegalArgumentException("Contact with id " + command.id() + " does not exist"));

        Contact updatedContact = contact.updateContact(new Email(command.email()), new Name(command.name()), command.customFields(), command.subscribed());
        return contactRepository.save(updatedContact);
    }


}

