package com.mailboom.api.domain.model;

import com.mailboom.api.domain.model.valueobjects.*;
import lombok.Getter;

@Getter
public class User {
    private final UserId id;
    private final Email email;
    private final Name name;
    private final PasswordHash password;
    private final PlanType plan;
    private final EmailCounter emailsSentThisMonth;
    private final Role role;

    private User(UserId id, Email email, Name name, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.plan = plan;
        this.emailsSentThisMonth = emailsSentThisMonth;
        this.role = role;
    }

    public static User create(UserId id, Email email, Name name, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth, Role role) {
        return new User(id, email,name, password, plan, emailsSentThisMonth, role);
    }

    // Sobrecarga para crear usuarios normales por defecto
    public static User create(UserId id, Email email, Name name, PasswordHash password, EmailCounter emailsSentThisMonth) {
        return new User(id, email, name , password, PlanType.FREE, emailsSentThisMonth, Role.USER);
    }

    public User incrementEmailsSent() {
        return new User(id, email, name , password, plan, emailsSentThisMonth.increment(), role);
    }

    public boolean canSendMoreEmails(int quantity) {
        int limit = plan.getEmailLimit();
        return emailsSentThisMonth.isWithinLimit(limit, quantity);
    }

    public boolean isPlanExpired() {
        return plan.isExpired();
    }

    public User resetMonthlyEmails() {
        return new User(id, email, name , password, plan, EmailCounter.zero(), role);
    }
}
