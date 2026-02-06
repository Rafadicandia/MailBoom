package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.UpdateContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;

public interface UpdateContactListUseCase {
    ContactList execute(UpdateContactListCommand command);
}
