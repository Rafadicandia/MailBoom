package com.mailboom.api.application.contact.port.in;

import com.mailboom.api.application.contact.usecase.command.GetAllContactsFromListCommand;
import com.mailboom.api.domain.model.contact.Contact;

import java.util.List;

public interface GetAllContactsFromListUseCase {
    List<Contact> execute(GetAllContactsFromListCommand command);
}
