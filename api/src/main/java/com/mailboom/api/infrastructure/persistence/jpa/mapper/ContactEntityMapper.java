package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactEntity;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactListEntity;
import org.springframework.stereotype.Component;

@Component
public class ContactEntityMapper {
    public ContactEntity toEntity(Contact contact) {
        ContactListEntity listEntity = new ContactListEntity();
        listEntity.setId(contact.getListId().value());

        return ContactEntity.builder()
                .id(contact.getId().value())
                .contactListId(listEntity)
                .email(contact.getEmail().toString())
                .name(contact.getName().value())
                .customFields(contact.getCustomFields())
                .subscribed(contact.isSubscribed())
                .build();
    }

    public Contact toDomain(ContactEntity entity) {
        return Contact.create(
                new ContactId(entity.getId()),
                new ContactListId(entity.getContactListId().getId()),
                new Email(entity.getEmail()),
                new Name(entity.getName()),
                entity.getCustomFields(),
                entity.isSubscribed()
        );
    }
}
