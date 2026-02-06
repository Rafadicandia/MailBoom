package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;
import com.mailboom.api.application.contact.usecase.command.UpdateContactCommand;
import com.mailboom.api.domain.model.contact.Contact;

public interface UpdateContactUseCase {
     Contact execute(UpdateContactCommand command);
}
