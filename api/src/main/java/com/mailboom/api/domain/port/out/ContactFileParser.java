package com.mailboom.api.domain.port.out;

import com.mailboom.api.infrastructure.dto.ContactData;

import java.io.InputStream;
import java.util.function.Consumer;

public interface ContactFileParser {
    void parse(InputStream inputStream, Consumer<ContactData> consumer);
}
