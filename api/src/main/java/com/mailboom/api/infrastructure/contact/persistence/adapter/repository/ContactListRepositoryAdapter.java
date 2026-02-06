package com.mailboom.api.infrastructure.contact.persistence.adapter.repository;

import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.infrastructure.contact.persistence.jpa.entity.ContactListEntity;
import com.mailboom.api.infrastructure.contact.persistence.jpa.mapper.ContactListEntityMapper;
import com.mailboom.api.infrastructure.contact.persistence.jpa.repository.SpringDataContactListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactListRepositoryAdapter implements ContactListRepository {

    private final SpringDataContactListRepository jpaRepository;
    private final ContactListEntityMapper contactListEntityMapper;

    @Override
    public Optional<ContactList> findById(ContactListId id) {
        return jpaRepository.findById(id.value()).map(contactListEntityMapper::toDomain);
    }

    @Override
    public List<ContactList> findAllByUserId(UserId userId) {
        return jpaRepository.findAllByUser_Id(userId.value()).stream()
                .map(contactListEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ContactList save(ContactList contactList) {
        ContactListEntity entity = contactListEntityMapper.toEntity(contactList);
        ContactListEntity savedEntity = jpaRepository.save(entity);
        return contactListEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(ContactListId id) {
        jpaRepository.deleteById(id.value());

    }



}
