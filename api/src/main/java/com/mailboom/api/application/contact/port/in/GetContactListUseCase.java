package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.GetContactListCommand;
import com.mailboom.api.domain.model.contact.ContactList;

public interface GetContactListUseCase {
    ContactList execute(GetContactListCommand command);

}
