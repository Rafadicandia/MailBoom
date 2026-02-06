package com.mailboom.api.domain.model.user;

import com.mailboom.api.domain.model.common.valueobjects.Email;
import com.mailboom.api.domain.model.common.valueobjects.Name;
import com.mailboom.api.domain.model.contact.valueobjects.ContactListId;
import com.mailboom.api.domain.model.user.valueobjects.*;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class User {
    private final UserId id;
    private final Email email;
    private final Name name;
    private final PasswordHash password;
    private final PlanType plan;
    private final EmailCounter emailsSentThisMonth;
    private final Role role;
    private final Set<ContactListId> contactLists;

    private User(UserId id, Email email, Name name, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth, Role role, Set<ContactListId> contactLists) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.plan = plan;
        this.emailsSentThisMonth = emailsSentThisMonth;
        this.role = role;
        this.contactLists = contactLists;
    }

    public static User create(UserId id, Email email, Name name, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth, Role role, Set<ContactListId> contactLists) {
        return new User(id, email, name, password, plan, emailsSentThisMonth, role, contactLists);
    }

    // Sobrecarga para crear usuarios normales por defecto
    public static User create(UserId id, Email email, Name name, PasswordHash password, EmailCounter emailsSentThisMonth) {
        return new User(id, email, name, password, PlanType.FREE, emailsSentThisMonth, Role.USER, new HashSet<>());
    }

    public static User createAdmin(UserId id, Email email, Name name, PasswordHash password){
        return new User(id, email, name, password, PlanType.ENTERPRISE, EmailCounter.zero(), Role.ADMIN, new HashSet<>());

    }

    public User incrementEmailsSent() {
        return new User(id, email, name, password, plan, emailsSentThisMonth.increment(), role, contactLists);
    }

    public boolean canSendMoreEmails(int quantity) {
        int limit = plan.getEmailLimit();
        return emailsSentThisMonth.isWithinLimit(limit, quantity);
    }

    public boolean isPlanExpired() {
        return plan.isExpired();
    }

    public User resetMonthlyEmails() {
        return new User(id, email, name, password, plan, EmailCounter.zero(), role, contactLists);
    }

    public User addContactList(ContactListId contactListId) {
        Set<ContactListId> updatedLists = new HashSet<>(this.contactLists);
        updatedLists.add(contactListId);
        return new User(id, email, name, password, plan, emailsSentThisMonth, role, updatedLists);
    }

    public User removeContactList(ContactListId contactListId) {
        Set<ContactListId> updatedLists = this.contactLists.stream()
                .filter(id -> !id.equals(contactListId))
                .collect(Collectors.toSet());
        return new User(id, email, name, password, plan, emailsSentThisMonth, role, updatedLists);
    }
}
