package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactListRepository {
    Optional<ContactList> findById(ContactListId id);
    List<ContactList> findAllByUserId(UserId userId);
    ContactList save(ContactList contactList);
    void deleteById(ContactListId id);

    List<ContactList> findAll();
}
