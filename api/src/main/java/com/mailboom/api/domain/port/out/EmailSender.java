package com.mailboom.api.domain.port.out;

public interface EmailSender {
    void send(String to, String subject, String body);
}
