package com.mailboom.api.infrastructure.controller;

import com.mailboom.api.application.contact.port.in.CreateContactUseCase;
import com.mailboom.api.application.contact.port.in.DeleteContactUseCase;
import com.mailboom.api.application.contact.usecase.command.CreateContactCommand;
import com.mailboom.api.application.contact.usecase.command.DeleteContactCommand;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.infrastructure.dto.DeleteContactRequest;
import com.mailboom.api.infrastructure.dto.NewContactRequest;
import com.mailboom.api.infrastructure.dto.NewContactResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final CreateContactUseCase createContactUseCase;
    private final DeleteContactUseCase deleteContactUseCase;

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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteContact(@PathVariable DeleteContactRequest request) {

        DeleteContactCommand deleteContactCommand = new DeleteContactCommand(request.contactId());
        deleteContactUseCase.execute(deleteContactCommand);

        return ResponseEntity.noContent().build();


    }


}
