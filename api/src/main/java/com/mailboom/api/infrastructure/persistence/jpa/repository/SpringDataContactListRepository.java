package com.mailboom.api.infrastructure.persistence.jpa.repository;

import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataContactListRepository extends JpaRepository<ContactListEntity, UUID> {
    List<ContactListEntity> findAllByOwnerId(UUID ownerId);
}
