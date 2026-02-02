package com.mailboom.api.domain.model;

import com.mailboom.api.domain.exception.ContactListMustHaveNameException;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.Name;
import com.mailboom.api.domain.model.valueobjects.UserId;

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

    public ContactList updateName(String newName) {
        if (newName == null || newName.isBlank()) throw new ContactListMustHaveNameException("Name required");
        return new ContactList(id, owner, new Name(newName), totalContacts);
    }
}
