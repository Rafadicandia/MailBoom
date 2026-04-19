package com.mailboom.api.domain.model.common.valueobjects;

import com.mailboom.api.domain.exception.InvalidPhoneException;

import java.util.regex.Pattern;

public record Phone(String phone) {

    private static final Pattern E164_PATTERN = Pattern.compile(
            "^\\+[1-9]\\d{0,2}\\d{5,14}$"
    );

    public Phone {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Phone cannot be null or empty");
        }
        if (!E164_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneException("Invalid phone format. Must be E.164 format (e.g., +34600000000)");
        }
    }

    public static Phone fromString(String phone) {
        return new Phone(phone);
    }

    @Override
    public String toString() {
        return phone;
    }
}
