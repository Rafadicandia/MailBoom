package com.mailboom.api.infrastructure.user.controller;

import com.mailboom.api.application.admin.in.port.*;
import com.mailboom.api.application.admin.usecase.command.GetAllUsersCommand;
import com.mailboom.api.application.campaign.usecase.command.GetCampaignCommand;
import com.mailboom.api.application.contact.port.in.*;
import com.mailboom.api.application.contact.usecase.command.*;
import com.mailboom.api.application.user.in.port.DeleteUserUseCase;
import com.mailboom.api.application.user.in.port.GetUserUseCase;
import com.mailboom.api.application.user.in.port.UpdateUserUseCase;
import com.mailboom.api.application.user.usecase.command.DeleteUserCommand;
import com.mailboom.api.application.user.usecase.command.GetUserCommand;
import com.mailboom.api.application.user.usecase.command.UpdateUserCommand;
import com.mailboom.api.domain.model.campaign.Campaign;
import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.user.User;
import com.mailboom.api.infrastructure.campaign.dto.CampaignDataResponse;
import com.mailboom.api.infrastructure.common.dto.GeneralMetricsDTO;
import com.mailboom.api.infrastructure.contact.dto.*;
import com.mailboom.api.infrastructure.user.dto.UpdateUserRequest;
import com.mailboom.api.infrastructure.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin operations for managing users, campaigns and contacts")
public class AdminController {

    //metrics
    private final GetGeneralMetricsUseCase getAccountGeneralMetrics;

    // User use cases
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    // Campaign use cases
    private final GetAllCampaignsUseCase getAllCampaignsUseCase;
    private final GetCampaignDetailsUseCase getCampaignDetailsUseCase;

    // Contact use cases
    private final GetContactListUseCase getContactListUseCase;
    private final GetContactListFromUserUseCase getContactListsFromOwnerUseCase;
    private final GetAllContactsFromListUseCase getAllContactsFromListUseCase;
    private final GetContactUseCase getContactUseCase;
    private final CreateContactListUseCase createContactListUseCase;
    private final UpdateContactListUseCase updateContactListUseCase;
    private final DeleteContactListUseCase deleteContactListUseCase;
    private final CreateContactUseCase createContactUseCase;
    private final UpdateContactUseCase updateContactUseCase;
    private final DeleteContactUseCase deleteContactUseCase;
    private final GetAllContactListsUseCase getAllContactListsUseCase;

    // ========== USER ENDPOINTS ==========

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        
        List<User> users = getAllUsersUseCase.execute(new GetAllUsersCommand(null));

        int start = Math.min((int) PageRequest.of(page, size).getOffset(), users.size());
        int end = Math.min((start + size), users.size());

        List<UserResponse> userResponses = users.subList(start, end).stream()
                .map(UserResponse::from)
                .toList();

        Page<UserResponse> pageResponse = new PageImpl<>(userResponses, PageRequest.of(page, size), users.size());

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        User user = getUserUseCase.execute(new GetUserCommand(id));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's information")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request
    ) {
        UpdateUserCommand command = new UpdateUserCommand(
                id,
                request.name(),
                request.email(),
                request.password()
        );
        User updatedUser = updateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        deleteUserUseCase.execute(new DeleteUserCommand(id));
        return ResponseEntity.noContent().build();
    }

    // ========== CAMPAIGN ENDPOINTS ==========

    @GetMapping("/campaigns")
    @Operation(summary = "Get all campaigns", description = "Retrieve all campaigns with pagination")
    public ResponseEntity<Page<CampaignDataResponse>> getAllCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        
        List<Campaign> campaigns = getAllCampaignsUseCase.execute();

        int start = Math.min((int) PageRequest.of(page, size).getOffset(), campaigns.size());
        int end = Math.min((start + size), campaigns.size());

        List<CampaignDataResponse> campaignResponses = campaigns.subList(start, end).stream()
                .map(campaign -> new CampaignDataResponse(
                        campaign.getId().value().toString(),
                        campaign.getOwner().value().toString(),
                        campaign.getSubject().value(),
                        campaign.getHtmlContent().value(),
                        campaign.getSenderIdentity().clientName(),
                        campaign.getRecipientList().value().toString(),
                        campaign.getStatus().name(),
                        campaign.getCreatedAt().toString()
                ))
                .toList();

        Page<CampaignDataResponse> pageResponse = new PageImpl<>(
                campaignResponses,
                PageRequest.of(page, size),
                campaigns.size()
        );

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/campaigns/{id}")
    @Operation(summary = "Get campaign by ID", description = "Retrieve a specific campaign by its ID")
    public ResponseEntity<CampaignDataResponse> getCampaignById(@PathVariable String id) {
        Campaign campaign = getCampaignDetailsUseCase.execute(new GetCampaignCommand(id));
        return ResponseEntity.ok(new CampaignDataResponse(
                campaign.getId().value().toString(),
                campaign.getOwner().value().toString(),
                campaign.getSubject().value(),
                campaign.getHtmlContent().value(),
                campaign.getSenderIdentity().clientName(),
                campaign.getRecipientList().value().toString(),
                campaign.getStatus().name(),
                campaign.getCreatedAt().toString()
        ));
    }

    // ========== CONTACT LIST ENDPOINTS ==========

    @GetMapping("/contacts/lists")
    @Operation(summary = "Get all contact lists", description = "Retrieve all contact lists from all users with pagination")
    public ResponseEntity<Page<ContactListDataResponse>> getAllContactLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userId
    ) {
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        
        List<ContactList> contactLists = getAllContactListsUseCase.execute();


        int start = Math.min((int) PageRequest.of(page, size).getOffset(), contactLists.size());
        int end = Math.min((start + size), contactLists.size());

        List<ContactListDataResponse> listResponses = contactLists.subList(start, end).stream()
                .map(list -> new ContactListDataResponse(
                        list.getId().toString(),
                        list.getName().toString(),
                        list.getOwner().toString(),
                        list.getTotalContacts()
                ))
                .toList();

        Page<ContactListDataResponse> pageResponse = new PageImpl<>(
                listResponses,
                PageRequest.of(page, size),
                contactLists.size()
        );

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/contacts/lists/{id}")
    @Operation(summary = "Get contact list by ID", description = "Retrieve a specific contact list by its ID")
    public ResponseEntity<ContactListDataResponse> getContactListById(@PathVariable String id) {
        GetContactListCommand command = new GetContactListCommand(id);
        ContactList contactList = getContactListUseCase.execute(command);
        return ResponseEntity.ok(new ContactListDataResponse(
                contactList.getId().toString(),
                contactList.getName().toString(),
                contactList.getOwner().toString(),
                contactList.getTotalContacts()
        ));
    }

    @PostMapping("/contacts/lists")
    @Operation(summary = "Create contact list", description = "Create a new contact list for a user")
    public ResponseEntity<NewContactListResponse> createContactList(@RequestBody NewContactListRequest request) {
        CreateContactListCommand command = new CreateContactListCommand(
                request.name(),
                UUID.fromString(request.ownerId())
        );
        ContactList newList = createContactListUseCase.execute(command);
        return ResponseEntity.ok(new NewContactListResponse(
                newList.getId().toString(),
                newList.getName().toString(),
                newList.getOwner().toString()
        ));
    }

    @PutMapping("/contacts/lists/{id}")
    @Operation(summary = "Update contact list", description = "Update an existing contact list")
    public ResponseEntity<UpdateContactListResponse> updateContactList(
            @PathVariable String id,
            @RequestBody UpdateContactListRequest request
    ) {
        UpdateContactListCommand command = new UpdateContactListCommand(
                id,
                request.name(),
                request.ownerId()
        );
        ContactList updatedList = updateContactListUseCase.execute(command);
        return ResponseEntity.ok(new UpdateContactListResponse(
                updatedList.getId().toString(),
                updatedList.getName().toString(),
                updatedList.getOwner().toString(),
                updatedList.getTotalContacts()
        ));
    }

    @DeleteMapping("/contacts/lists/{id}")
    @Operation(summary = "Delete contact list", description = "Delete a contact list by its ID")
    public ResponseEntity<Void> deleteContactList(@PathVariable String id) {
        DeleteContactListCommand command = new DeleteContactListCommand(id);
        deleteContactListUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    // ========== CONTACT ENDPOINTS ==========

    @GetMapping("/contacts/lists/{listId}/contacts")
    @Operation(summary = "Get contacts from list", description = "Retrieve all contacts from a specific list with pagination")
    public ResponseEntity<Page<ContactDataResponse>> getContactsFromList(
            @PathVariable String listId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        
        GetAllContactsFromListCommand command = new GetAllContactsFromListCommand(listId);
        List<Contact> contacts = getAllContactsFromListUseCase.execute(command);

        int start = Math.min((int) PageRequest.of(page, size).getOffset(), contacts.size());
        int end = Math.min((start + size), contacts.size());

        List<ContactDataResponse> contactResponses = contacts.subList(start, end).stream()
                .map(contact -> new ContactDataResponse(
                        contact.getId().toString(),
                        contact.getListId().toString(),
                        contact.getEmail().toString(),
                        contact.getName().toString(),
                        contact.getCustomFields(),
                        contact.isSubscribed()
                ))
                .toList();

        Page<ContactDataResponse> pageResponse = new PageImpl<>(
                contactResponses,
                PageRequest.of(page, size),
                contacts.size()
        );

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/contacts/{id}")
    @Operation(summary = "Get contact by ID", description = "Retrieve a specific contact by its ID")
    public ResponseEntity<ContactDataResponse> getContactById(@PathVariable String id) {
        GetContactCommand command = new GetContactCommand(id);
        Contact contact = getContactUseCase.execute(command);
        return ResponseEntity.ok(new ContactDataResponse(
                contact.getId().toString(),
                contact.getListId().toString(),
                contact.getEmail().toString(),
                contact.getName().toString(),
                contact.getCustomFields(),
                contact.isSubscribed()
        ));
    }

    @PostMapping("/contacts")
    @Operation(summary = "Create contact", description = "Create a new contact in a list")
    public ResponseEntity<NewContactResponse> createContact(@RequestBody NewContactRequest request) {
        CreateContactCommand command = new CreateContactCommand(
                request.contactListId(),
                request.email(),
                request.name(),
                request.customFields(),
                request.subscribed()
        );
        Contact newContact = createContactUseCase.execute(command);
        return ResponseEntity.ok(new NewContactResponse(
                newContact.getId().toString(),
                newContact.getListId().toString(),
                newContact.getEmail().toString(),
                newContact.getName().toString(),
                newContact.getCustomFields(),
                newContact.isSubscribed()
        ));
    }

    @PutMapping("/contacts/{id}")
    @Operation(summary = "Update contact", description = "Update an existing contact")
    public ResponseEntity<UpdateContactResponse> updateContact(
            @PathVariable String id,
            @RequestBody UpdateContactRequest request
    ) {
        UpdateContactCommand command = new UpdateContactCommand(
                id,
                request.email(),
                request.name(),
                request.customFields(),
                request.subscribed()
        );
        Contact updatedContact = updateContactUseCase.execute(command);
        return ResponseEntity.ok(new UpdateContactResponse(
                updatedContact.getId().toString(),
                updatedContact.getListId().toString(),
                updatedContact.getEmail().toString(),
                updatedContact.getName().toString(),
                updatedContact.getCustomFields(),
                updatedContact.isSubscribed()
        ));
    }

    @DeleteMapping("/contacts/{id}")
    @Operation(summary = "Delete contact", description = "Delete a contact by its ID")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        DeleteContactCommand command = new DeleteContactCommand(id);
        deleteContactUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/metrics")
    public ResponseEntity<GeneralMetricsDTO> getSummary() {
        return ResponseEntity.ok(getAccountGeneralMetrics.execute());
    }
}
