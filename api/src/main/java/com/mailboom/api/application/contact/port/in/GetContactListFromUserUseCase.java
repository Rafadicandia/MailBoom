package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.GetContactListFromUserCommand;
import com.mailboom.api.domain.model.contact.ContactList;

import java.util.List;

public interface GetContactListFromUserUseCase {
    List<ContactList> execute(GetContactListFromUserCommand command);
}
