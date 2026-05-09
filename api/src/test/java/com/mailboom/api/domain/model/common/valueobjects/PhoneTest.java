package com.mailboom.api.domain.model.common.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneTest {
    @Test
    void shouldCreatePhoneWhenFormatIsValid() {
        String validPhone = "+34600000000";
        Phone phone = new Phone(validPhone);
        assertEquals(validPhone, phone.phone());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        assertThrows(com.mailboom.api.domain.exception.InvalidPhoneException.class, () -> new Phone(null));
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsBlank() {
        assertThrows(com.mailboom.api.domain.exception.InvalidPhoneException.class, () -> new Phone("   "));
    }

    @Test
    void shouldThrowExceptionWhenFormatIsInvalid() {
        assertThrows(com.mailboom.api.domain.exception.InvalidPhoneException.class, () -> new Phone("600000000"));
        assertThrows(com.mailboom.api.domain.exception.InvalidPhoneException.class, () -> new Phone("+0123456"));
        assertThrows(com.mailboom.api.domain.exception.InvalidPhoneException.class, () -> new Phone("+34abc123"));
    }

    @Test
    void shouldReturnPhoneStringOnToString() {
        String phoneStr = "+12345678901";
        Phone phone = new Phone(phoneStr);
        assertEquals(phoneStr, phone.toString());
    }

    @Test
    void shouldCreatePhoneFromStaticMethod() {
        String phoneStr = "+5491122334455";
        Phone phone = Phone.fromString(phoneStr);
        assertEquals(phoneStr, phone.phone());
    }


}