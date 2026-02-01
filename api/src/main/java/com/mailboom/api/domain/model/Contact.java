package com.mailboom.api.domain.model;

import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.model.valueobjects.Email;
import com.mailboom.api.domain.model.valueobjects.Name;
import lombok.Getter;

import java.util.Map;

@Getter
public class Contact {

    private final ContactId id;
    private final  Email email;
    private final Name name;
    private final Map<String, Object> customFields;
    private final boolean subscribed;

    private Contact(ContactId id, Email email, Name name, Map<String, Object> customFields, boolean subscribed) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.customFields = customFields;
        this.subscribed = subscribed;
    }

    public static Contact create(ContactId id, Email email, Name name, Map<String, Object> customFields, boolean subscribed){
        return new Contact(id, email, name, customFields, subscribed);
    }

    public Contact subscribe(Contact contact){
        return Contact.create(id, email, name, customFields, true);
    }

    public Contact unSubscribe(Contact contact){
        return Contact.create(id, email, name, customFields, false);
    }


}
