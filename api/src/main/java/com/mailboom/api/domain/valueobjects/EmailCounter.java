package com.mailboom.api.domain.valueobjects;

public record EmailCounter(int amountOfEmails) {

    public EmailCounter {
        if (amountOfEmails < 0) {
            throw new IllegalArgumentException("Email count cannot be negative");
        }
    }

    public static EmailCounter zero() {
        return new EmailCounter(0);
    }

    public EmailCounter increment() {
        return new EmailCounter(amountOfEmails + 1);
    }

    public EmailCounter add(int quantity) {
        return new EmailCounter(amountOfEmails + quantity);
    }

    public boolean isWithinLimit(int limit, int additionalQuantity) {
        return (amountOfEmails + additionalQuantity) <= limit;
    }
}
