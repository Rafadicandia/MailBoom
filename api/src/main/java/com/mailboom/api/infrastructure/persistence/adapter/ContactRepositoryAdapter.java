package com.mailboom.api.infrastructure.persistence.adapter;

import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.ContactList;
import com.mailboom.api.domain.model.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import com.mailboom.api.domain.port.out.UserRepository;
import com.mailboom.api.infrastructure.exception.UserNotFoundException;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.ContactEntityMapper;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ContactRepositoryAdapter implements ContactRepository {

    private final SpringDataContactRepository contactRepository;
    private final ContactEntityMapper contactEntityMapper;
    private final ContactListRepository contactListRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<Contact> findById(UUID id) {
        return contactRepository.findById(id).map(contactEntityMapper::toDomain);
    }

    @Override
    public List<Contact> findAllByUserId(UUID userId) {
        if (userRepository.findById(new UserId(userId)) == null) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }

        List<ContactList> userContactLists = contactListRepository.findAllByUserId(userId);
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
    public void deleteById(UUID id) {
        contactRepository.deleteById(id);
    }

    @Override
    public void saveAll(List<Contact> contacts) {
        List<ContactEntity> entities = contacts.stream()
                .map(contactEntityMapper::toEntity)
                .collect(Collectors.toList());
        contactRepository.saveAll(entities);
    }
}
