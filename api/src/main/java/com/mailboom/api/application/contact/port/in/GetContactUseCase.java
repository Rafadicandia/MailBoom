package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.GetContactCommand;
import com.mailboom.api.domain.model.contact.Contact;

public interface GetContactUseCase {
    Contact execute(GetContactCommand command);
}
