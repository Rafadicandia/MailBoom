package com.mailboom.api.infrastructure.persistence.adapter;

import com.mailboom.api.domain.model.ContactList;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.model.valueobjects.UserId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactListEntity;
import com.mailboom.api.infrastructure.persistence.jpa.mapper.ContactListEntityMapper;
import com.mailboom.api.infrastructure.persistence.jpa.repository.SpringDataContactListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactListRepositoryAdapter implements ContactListRepository {

    private final SpringDataContactListRepository jpaRepository;
    private final ContactListEntityMapper contactListEntityMapper;

    @Override
    public Optional<ContactList> findById(UUID id) {
        return jpaRepository.findById(id).map(contactListEntityMapper::toDomain);
    }

    @Override
    public List<ContactList> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByOwnerId(userId).stream()
                .map(contactListEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ContactList save(ContactList contactList) {
        ContactListEntity entity = contactListEntityMapper.toEntity(contactList);
        ContactListEntity savedEntity = jpaRepository.save(entity);
        return contactListEntityMapper.toDomain(savedEntity);
    }

}
