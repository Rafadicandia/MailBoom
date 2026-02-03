package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.Contact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository {
    Optional<Contact> findById(UUID id);
    List<Contact> findAllByUserId(UUID userId);
    Contact save(Contact contact);
    void deleteById(UUID id);
    void saveAll(List<Contact> contacts);
}
