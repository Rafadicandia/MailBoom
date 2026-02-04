package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.ContactList;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.model.valueobjects.UserId;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactListEntity;
import org.springframework.stereotype.Component;

@Component
public class ContactListEntityMapper {

    public ContactListEntity toEntity(ContactList contactList) {
        return ContactListEntity.builder()
                .id(contactList.getId().value())
                .name(contactList.getName().toString())
                .totalContacts(contactList.getTotalContacts())
                .build();

    }

    public ContactList toDomain(ContactListEntity contactListEntity) {
        return ContactList.reCreate(
                new ContactListId(contactListEntity.getId()),
                new UserId(contactListEntity.getUser().getId()),
                new Name(contactListEntity.getName()),
                contactListEntity.getTotalContacts()
        );
    }
}
