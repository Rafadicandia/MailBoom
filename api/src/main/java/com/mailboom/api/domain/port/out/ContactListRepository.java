package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.contact.ContactList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactListRepository {
    Optional<ContactList> findById(UUID id);
    List<ContactList> findAllByUserId(UUID userId);
    ContactList save(ContactList contactList);
}
