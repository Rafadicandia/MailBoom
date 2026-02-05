package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;

public interface CreateContactListUseCase {
    ContactList execute(CreateContactListCommand command);
}
