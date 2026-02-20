package com.mailboom.api.application.admin.in.port;

import com.mailboom.api.domain.model.contact.ContactList;

import java.util.List;

public interface GetAllContactListsUseCase {
    List<ContactList> execute();

}
