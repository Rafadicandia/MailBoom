package com.mailboom.api.infrastructure.security;

import com.mailboom.api.domain.model.contact.Contact;
import com.mailboom.api.domain.model.contact.ContactList;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final ContactListRepository contactListRepository;
    private final ContactRepository contactRepository;

    public boolean isListOwner(Authentication authentication, String listIdStr) {
        if (isAdmin(authentication)) return true;
        
        try {
            UUID listIdUuid = UUID.fromString(listIdStr);
            ContactListId listId = new ContactListId(listIdUuid);
            
            return contactListRepository.findById(listId)
                    .map(list -> list.getOwner().value().toString().equals(getUserId(authentication)))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isContactOwner(Authentication authentication, String contactIdStr) {
        if (isAdmin(authentication)) return true;

        try {
            UUID contactIdUuid = UUID.fromString(contactIdStr);
            ContactId contactId = new ContactId(contactIdUuid);

            return contactRepository.findById(contactId)
                    .flatMap(contact -> contactListRepository.findById(contact.getListId()))
                    .map(list -> list.getOwner().value().toString().equals(getUserId(authentication)))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isOwner(Authentication authentication, String userIdStr) {
        if (isAdmin(authentication)) return true;
        return getUserId(authentication).equals(userIdStr);
    }

    private String getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser().getId().value().toString();
        }
        return null;
    }
    
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
