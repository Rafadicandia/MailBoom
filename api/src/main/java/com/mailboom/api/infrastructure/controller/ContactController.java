package com.mailboom.api.infrastructure.controller;

import com.mailboom.api.application.contact.port.in.*;
import com.mailboom.api.application.contact.usecase.command.*;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


}
