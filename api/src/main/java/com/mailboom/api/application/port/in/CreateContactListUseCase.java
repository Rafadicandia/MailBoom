package com.mailboom.api.application.port.in;

import com.mailboom.api.application.usecase.command.CreateContactListCommand;
import com.mailboom.api.domain.model.ContactList;

public interface CreateContactListUseCase {
    ContactList execute(CreateContactListCommand command);
}
