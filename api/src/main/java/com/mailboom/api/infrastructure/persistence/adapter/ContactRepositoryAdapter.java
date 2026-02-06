package com.mailboom.api.infrastructure.persistence.adapter;

import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.common.exception.UserNotFoundException;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.ContactEntityMapper;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ContactRepositoryAdapter implements ContactRepository {

    private final SpringDataContactRepository contactRepository;
    private final ContactEntityMapper contactEntityMapper;
    private final ContactListRepository contactListRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<Contact> findById(ContactId id) {
        return contactRepository.findById(id.value()).map(contactEntityMapper::toDomain);
    }

    @Override
    public List<Contact> findAllByUserId(UserId userId) {
        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }

        List<ContactList> userContactLists = contactListRepository.findAllByUserId(new UserId(userId.value()));
        if (userContactLists.isEmpty()) {
            return Collections.emptyList();
        }

        return userContactLists.stream()
                .flatMap(list -> contactRepository.findAllByContactListId_Id(list.getId().value()).stream())
                .map(contactEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Contact save(Contact contact) {
        ContactEntity entity = contactEntityMapper.toEntity(contact);
        ContactEntity savedEntity = contactRepository.save(entity);
        return contactEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(ContactId id) {
        contactRepository.deleteById(id.value());
    }

    @Override
    public void saveAll(List<Contact> contacts) {
        List<ContactEntity> entities = contacts.stream()
                .map(contactEntityMapper::toEntity)
                .collect(Collectors.toList());
        contactRepository.saveAll(entities);
    }

    @Override
    public List<Contact> findAllByContactListId(ContactListId contactListId) {
        return contactRepository.findAllByContactListId_Id(contactListId.value()).stream()
                .map(contactEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Contact> findByEmail(Email email) {
        return contactRepository.findByEmail(email.toString()).map(contactEntityMapper::toDomain);
    }
}
