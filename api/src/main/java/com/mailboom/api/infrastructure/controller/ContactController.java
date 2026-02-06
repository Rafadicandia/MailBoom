package com.mailboom.api.infrastructure.controller;

import com.mailboom.api.application.contact.port.in.*;
import com.mailboom.api.application.contact.usecase.command.*;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final CreateContactUseCase createContactUseCase;
    private final DeleteContactUseCase deleteContactUseCase;
    private final UpdateContactUseCase updateContactUseCase;
    private final GetContactUseCase getContactUseCase;
    private final DeleteContactListUseCase deleteContactListUseCase;
    private final CreateContactListUseCase createContactListUseCase;
    private final UpdateContactListUseCase updateContactListUseCase;
    private final GetContactListUseCase getContactListUseCase;
    private final GetContactListFromUserUseCase getContactListsFromOwnerUseCase;




    //new contact
    @PostMapping("/new")
    public ResponseEntity<NewContactResponse> createContact(
            @RequestBody NewContactRequest request) {
        CreateContactCommand createContactCommand = new CreateContactCommand(
                request.contactListId(),
                request.email(),
                request.name(),
                new HashMap<>(request.customFields()),
                request.subscribed()

        );
        Contact newContact = createContactUseCase.execute(createContactCommand);
        return ResponseEntity.ok(new NewContactResponse(
                newContact.getId().toString(),
                newContact.getListId().toString(),
                newContact.getEmail().toString(),
                newContact.getName().toString(),
                newContact.getCustomFields(),
                newContact.isSubscribed()
        ));
    }

    //new contactList
    @PostMapping("/new/list")
    public ResponseEntity<NewContactListResponse> createContactList(
            @RequestBody NewContactListRequest request) {
        CreateContactListCommand createContactListCommand = new CreateContactListCommand(
                request.name(), UUID.fromString(request.ownerId()));

        ContactList newContactList = createContactListUseCase.execute(createContactListCommand);
        return ResponseEntity.ok(new NewContactListResponse(
                newContactList.getId().toString(),
                newContactList.getName().toString(),
                newContactList.getOwner().toString()));
    }
//Update Contact
    @PutMapping("/{id}/update")
    public ResponseEntity<UpdateContactResponse> updateContact(
            @PathVariable UUID id,
            @RequestBody UpdateContactRequest request) {
        UpdateContactCommand updateContactCommand = new UpdateContactCommand(
                id.toString(), // Corrected: Use the path variable ID
                request.email(),
                request.name(),
                new HashMap<>(request.customFields()),
                request.subscribed()
        );
        Contact updateContact = updateContactUseCase.execute(updateContactCommand);
        return ResponseEntity.ok(new UpdateContactResponse(
                updateContact.getId().toString(),
                updateContact.getListId().toString(),
                updateContact.getEmail().toString(),
                updateContact.getName().toString(),
                updateContact.getCustomFields(),
                updateContact.isSubscribed()));


    }
//updateContactList
    @PutMapping("/{id}/list/update")
    public ResponseEntity<UpdateContactListResponse> updateContactList(
            @PathVariable UUID id,
            @RequestBody UpdateContactListRequest request) {
        UpdateContactListCommand updateContactListCommand = new UpdateContactListCommand(
                id.toString(),
                request.name(),
                UUID.fromString(request.ownerId()).toString()
        );
        ContactList updateContactList = updateContactListUseCase.execute(updateContactListCommand);

        return ResponseEntity.ok(new UpdateContactListResponse(
                updateContactList.getId().toString(),
                updateContactList.getName().toString(),
                updateContactList.getOwner().toString(),
                updateContactList.getTotalContacts()));


    }


    //Delete contact
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteContact(@PathVariable UUID id) {

        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(id.toString());
        deleteContactUseCase.execute(deleteContactCommand);

        return ResponseEntity.noContent().build();


    }

    //Delete contact List
    @DeleteMapping("/{id}/list/delete")
    public ResponseEntity<Void> deleteContactList(@PathVariable UUID id) {

        DeleteContactListCommand deleteContactListCommand = new DeleteContactListCommand(id.toString());
        deleteContactListUseCase.execute(deleteContactListCommand);

        return ResponseEntity.noContent().build();

    }

//Get contact
    @GetMapping("/{id}")
    public ResponseEntity<ContactDataResponse> getContact(@PathVariable UUID id) {
        GetContactCommand getContactCommand = new GetContactCommand(id.toString());
        Contact contact = getContactUseCase.execute(getContactCommand);

        return ResponseEntity.ok(new ContactDataResponse(
                contact.getId().toString(),
                contact.getListId().toString(),
                contact.getEmail().toString(),
                contact.getName().toString(),
                contact.getCustomFields(),
                contact.isSubscribed()
        ));

    }

    //Get contactLit
    @GetMapping("/{id}/list")
    public ResponseEntity<ContactListDataResponse> getContactList(@PathVariable UUID id) {
        GetContactListCommand getContactListCommand = new GetContactListCommand(id.toString());
        ContactList contactList = getContactListUseCase.execute(getContactListCommand);
        return ResponseEntity.ok(new ContactListDataResponse(
                contactList.getId().toString(),
                contactList.getName().toString(),
                contactList.getOwner().toString(),
                contactList.getTotalContacts()));
    }

    //get contactLists from owner
    @GetMapping("/list/user/{id}")
    public ResponseEntity<List<ContactListDataResponse>> getContactListsFromOwner(@PathVariable UUID id) {
        GetContactListFromUserCommand getContactListsFromOwnerCommand = new GetContactListFromUserCommand(id.toString());
        List<ContactList> contactLists = getContactListsFromOwnerUseCase.execute(getContactListsFromOwnerCommand);
        return ResponseEntity.ok(contactLists.stream().map(
                contactList -> new ContactListDataResponse(
                        contactList.getId().toString(),
                        contactList.getName().toString(),
                        contactList.getOwner().toString(),
                        contactList.getTotalContacts()
                )).toList()
        );

    }



}
