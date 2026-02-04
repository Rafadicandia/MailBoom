package com.mailboom.api.infrastructure.persistence.jpa.repository;

import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataContactRepository extends JpaRepository<ContactEntity, UUID> {
    List<ContactEntity> findAllByContactListId_Id(UUID contactListId);
}
