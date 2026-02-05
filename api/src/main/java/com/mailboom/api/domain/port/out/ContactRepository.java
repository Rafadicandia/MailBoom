package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Email;
import com.mailboom.api.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository {
    Optional<Contact> findById(ContactId id);
    List<Contact> findAllByUserId(UserId userId);
    List<Contact> findAllByContactListId(ContactListId contactListId);
    Optional<Contact> findByEmail(Email email);
    Contact save(Contact contact);
    void deleteById(ContactId id);
    void saveAll(List<Contact> contacts);
}
