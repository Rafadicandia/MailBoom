package com.mailboom.api.domain.model;

import com.mailboom.api.domain.model.valueobjects.*;
import lombok.Getter;

@Getter
public class User {
    private final UserId id;
    private final Email email;
    private final PasswordHash password;
    private final PlanType plan;
    private final EmailCounter emailsSentThisMonth;

    private User(UserId id, Email email, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.plan = plan;
        this.emailsSentThisMonth = emailsSentThisMonth;
    }

    public static User create(UserId id, Email email, PasswordHash password, PlanType plan, EmailCounter emailsSentThisMonth) {
        return new User(id, email, password, plan, emailsSentThisMonth);
    }

    public User incrementEmailsSent() {
        return new User(id, email, password, plan, emailsSentThisMonth.increment());
    }

    public boolean canSendMoreEmails(int quantity) {
        int limit = plan.getEmailLimit();
        return emailsSentThisMonth.isWithinLimit(limit, quantity);
    }

    public boolean isPlanExpired() {
        return plan.isExpired();
    }

    public User resetMonthlyEmails() {
        return new User(id, email, password, plan, EmailCounter.zero());
    }

}
