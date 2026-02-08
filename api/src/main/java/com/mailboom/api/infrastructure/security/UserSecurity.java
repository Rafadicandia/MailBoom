package com.mailboom.api.infrastructure.security;

import com.mailboom.api.domain.model.campaign.valueobjects.CampaignId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactId;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.port.out.CampaignRepository;
import com.mailboom.api.domain.port.out.ContactListRepository;
import com.mailboom.api.domain.port.out.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private static final Logger logger = LoggerFactory.getLogger(UserSecurity.class);

    private final ContactListRepository contactListRepository;
    private final ContactRepository contactRepository;
    private final CampaignRepository campaignRepository;

    public boolean isListOwner(Authentication authentication, String listIdStr) {
        if (isAdmin(authentication)) return true;
        
        try {
            UUID listIdUuid = UUID.fromString(listIdStr);
            ContactListId listId = new ContactListId(listIdUuid);
            
            return contactListRepository.findById(listId)
                    .map(list -> {
                        String ownerId = list.getOwner().value().toString();
                        String userId = getUserId(authentication);
                        boolean isOwner = ownerId.equals(userId);
                        if (!isOwner) {
                            logger.warn("User {} is not owner of list {}. Owner is {}", userId, listIdStr, ownerId);
                        }
                        return isOwner;
                    })
                    .orElseGet(() -> {
                        logger.warn("List {} not found", listIdStr);
                        return false;
                    });
        } catch (Exception e) {
            logger.error("Error checking list ownership", e);
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
                    .map(list -> {
                        String ownerId = list.getOwner().value().toString();
                        String userId = getUserId(authentication);
                        boolean isOwner = ownerId.equals(userId);
                        if (!isOwner) {
                            logger.warn("User {} is not owner of contact {}. Owner is {}", userId, contactIdStr, ownerId);
                        }
                        return isOwner;
                    })
                    .orElseGet(() -> {
                        logger.warn("Contact {} not found or list not found", contactIdStr);
                        return false;
                    });
        } catch (Exception e) {
            logger.error("Error checking contact ownership", e);
            return false;
        }
    }

    public boolean isCampaignOwner(Authentication authentication, String campaignIdStr) {
        if (isAdmin(authentication)) return true;

        try {
            UUID campaignIdUuid = UUID.fromString(campaignIdStr);
            CampaignId campaignId = new CampaignId(campaignIdUuid);
            
            var campaign = campaignRepository.findById(campaignId);
            if (campaign == null) {
                logger.warn("Campaign {} not found", campaignIdStr);
                return false;
            }

            String ownerId = campaign.getOwner().value().toString();
            String userId = getUserId(authentication);
            boolean isOwner = ownerId.equals(userId);
            
            if (!isOwner) {
                logger.warn("User {} is not owner of campaign {}. Owner is {}", userId, campaignIdStr, ownerId);
            }
            return isOwner;
        } catch (Exception e) {
            logger.error("Error checking campaign ownership", e);
            return false;
        }
    }
    
    public boolean isOwner(Authentication authentication, String userIdStr) {
        if (isAdmin(authentication)) return true;
        String userId = getUserId(authentication);
        boolean isOwner = userId != null && userId.equals(userIdStr);
        
        if (!isOwner) {
            logger.warn("User {} is trying to access resource of user {}", userId, userIdStr);
        }
        return isOwner;
    }
    
    public boolean isOwner(Authentication authentication, UUID userId) {
        if (isAdmin(authentication)) return true;
        String authUserId = getUserId(authentication);
        boolean isOwner = authUserId != null && authUserId.equals(userId.toString());
        
        if (!isOwner) {
            logger.warn("User {} is trying to access resource of user {}", authUserId, userId);
        }
        return isOwner;
    }

    private String getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser().getId().value().toString();
        }
        logger.warn("Principal is not UserPrincipal: {}", authentication.getPrincipal());
        return null;
    }
    
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
