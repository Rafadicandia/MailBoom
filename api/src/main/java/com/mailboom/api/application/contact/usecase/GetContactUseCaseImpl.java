package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.GetContactUseCase;
import com.mailboom.api.application.contact.usecase.command.GetContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetContactUseCaseImpl implements GetContactUseCase {
    private final ContactRepository contactRepository;

    @Override
    public Contact execute(GetContactCommand command) {

        return contactRepository.findById(ContactId.fromString(command.id())).
                orElseThrow(() -> new IllegalArgumentException("Contact with id " + command.id() + " does not exist"));

    }
}
