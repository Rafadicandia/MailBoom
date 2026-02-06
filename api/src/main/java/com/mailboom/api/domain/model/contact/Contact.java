package com.mailboom.api.domain.model.contact;

import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import lombok.Getter;

import java.util.Map;

@Getter
public class Contact {

    private final ContactId id;
    private final ContactListId listId;
    private final  Email email;
    private final Name name;
    private final Map<String, Object> customFields;
    private final boolean subscribed;

    private Contact(ContactId id, ContactListId listId, Email email, Name name, Map<String, Object> customFields, boolean subscribed) {
        this.id = id;
        this.listId = listId;
        this.email = email;
        this.name = name;
        this.customFields = customFields;
        this.subscribed = subscribed;
    }

    public static Contact create(ContactId id, ContactListId listId, Email email, Name name, Map<String, Object> customFields, boolean subscribed){
        return new Contact(id, listId, email, name, customFields, subscribed);
    }

    public Contact updateContact(Email email, Name name, Map<String, Object> customFields, boolean subscribed){
        return new Contact(this.id, this.listId, email, name, customFields, subscribed);
    }

    public Contact subscribe(Contact contact){
        return Contact.create(id, listId, email, name, customFields, true);
    }

    public Contact unSubscribe(Contact contact){
        return Contact.create(id, listId, email, name, customFields, false);
    }


}
