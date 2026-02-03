package com.mailboom.api.domain.port.out;

public interface FileParserFactory {
    ContactFileParser getParser(String contentType);
}
