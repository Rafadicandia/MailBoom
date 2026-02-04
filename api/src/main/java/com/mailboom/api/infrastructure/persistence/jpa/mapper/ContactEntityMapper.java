package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.Contact;
import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Email;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactEntity;
import org.springframework.stereotype.Component;

@Component
public class ContactEntityMapper {
    public ContactEntity toEntity(Contact contact) {
        return ContactEntity.builder()
                .id(contact.getId().value())
                .email(contact.getEmail().email())
                .name(contact.getName().toString())
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
