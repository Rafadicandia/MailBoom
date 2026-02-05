package com.mailboom.api.application.contact.usecase;

import com.mailboom.api.application.contact.port.in.DeleteContactUseCase;
import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;
import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteContactUseCaseImpl implements DeleteContactUseCase {
    private final ContactRepository contactRepository;

    @Override
    public void execute(DeleteContactCommand command) {
        if (contactRepository.findById(ContactId.fromString(command.id())).isEmpty()) {
            throw new IllegalArgumentException("Contact with id " + command.id() + " does not exist");
        }
        contactRepository.deleteById(ContactId.fromString(command.id()));
        System.out.println("Contact with id " + command.id() + " deleted successfully");
    }
}
