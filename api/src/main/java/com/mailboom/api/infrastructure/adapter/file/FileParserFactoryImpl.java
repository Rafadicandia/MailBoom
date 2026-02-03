package com.mailboom.api.infrastructure.adapter.file;

import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.domain.port.out.FileParserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileParserFactoryImpl implements FileParserFactory {

    private final CsvContactFileParser csvParser;

    @Override
    public ContactFileParser getParser(String contentType) {
        if (contentType == null) {
             throw new IllegalArgumentException("Content type cannot be null");
        }
        
        if (contentType.contains("text/csv") || contentType.contains("application/vnd.ms-excel")) { // Basic check
            return csvParser;
        }
        
        throw new IllegalArgumentException("Unsupported content type: " + contentType);
    }
}
