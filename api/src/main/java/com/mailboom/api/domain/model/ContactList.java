package com.mailboom.api.domain.model;

import com.mailboom.api.domain.model.valueobjects.ContactId;
import com.mailboom.api.domain.model.valueobjects.ContactListId;
import com.mailboom.api.domain.model.valueobjects.UserId;

import java.util.Set;

public class ContactList {

    private final ContactListId id;
    private final UserId owner;
    private String name;
    private Set<ContactId> contacts;

    public ContactList(ContactListId id, UserId owner, String name, Set<ContactId> contacts) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.contacts = contacts;
    }

    public void addContact(ContactId contactId) {
        this.contacts.add(contactId);
    }

    public void removeContact(ContactId contactId) {
        this.contacts.remove(contactId);
    }

    public ContactListId getId() {
        return id;
    }

    public UserId getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ContactId> getContacts() {
        return contacts;
    }
}
