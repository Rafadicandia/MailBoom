package com.mailboom.api.infrastructure.persistence.jpa.mapper;

import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import com.mailboom.api.infrastructure.persistence.jpa.entity.ContactListEntity;
import com.mailboom.api.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ContactListEntityMapper {

    public ContactListEntity toEntity(ContactList contactList) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(contactList.getOwner().value());

        return ContactListEntity.builder()
                .id(contactList.getId().value())
                .user(userEntity)
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
