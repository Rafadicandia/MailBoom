package com.mailboom.api.domain.model.contact;

import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.common.valueobjects.Phone;
import lombok.Getter;

import java.util.Map;

@Getter
public class Contact {

    private final ContactId id;
    private final ContactListId listId;
    private final Email email;
    private final Name name;
    private final Phone phone;
    private final Map<String, Object> customFields;
    private final boolean subscribed;

    private Contact(ContactId id, ContactListId listId, Email email, Name name, Phone phone, Map<String, Object> customFields, boolean subscribed) {
        this.id = id;
        this.listId = listId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.customFields = customFields;
        this.subscribed = subscribed;
    }

    public static Contact create(ContactId id, ContactListId listId, Email email, Name name, Phone phone, Map<String, Object> customFields, boolean subscribed){
        return new Contact(id, listId, email, name, phone, customFields, subscribed);
    }

    public Contact updateContact(Email email, Name name, Phone phone, Map<String, Object> customFields, boolean subscribed){
        return new Contact(this.id, this.listId, email, name, phone, customFields, subscribed);
    }

    public Contact subscribe(){
        return new Contact(id, listId, email, name, phone, customFields, true);
    }

    public Contact unSubscribe(){
        return new Contact(id, listId, email, name, phone, customFields, false);
    }


}
