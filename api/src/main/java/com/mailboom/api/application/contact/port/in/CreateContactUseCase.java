package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.CreateContactCommand;
import com.mailboom.api.domain.model.contact.Contact;

public interface CreateContactUseCase {
    Contact execute(CreateContactCommand command);

}
