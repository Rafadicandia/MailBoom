package com.mailboom.api.domain.model.contact;

import com.mailboom.api.domain.exception.ContactListMustHaveNameException;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.user.valueobjects.UserId;
import lombok.Getter;

@Getter
public class ContactList {
    private final ContactListId id;
    private final UserId owner;
    private final Name name;
    private final long totalContacts;

    private ContactList(ContactListId id, UserId owner, Name name, long totalContacts) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.totalContacts = 0;
    }
    //We use this one for a new list
    public static ContactList create(UserId owner, Name name){
        if (name == null || name.toString().isBlank()) throw new ContactListMustHaveNameException("Name required");
        return new ContactList(ContactListId.generate(), owner, name, 0);
    }

    // We use this one for recreating the domain object
    public static ContactList reCreate(ContactListId id, UserId owner, Name name, long totalContacts){
        if (name == null || name.toString().isBlank()) throw new ContactListMustHaveNameException("Name required");
        return new ContactList(id, owner, name, totalContacts);
    }

    public ContactList updateName(String newName) {
        if (newName == null || newName.isBlank()) throw new ContactListMustHaveNameException("Name required");
        return new ContactList(id, owner, new Name(newName), totalContacts);
    }
}
