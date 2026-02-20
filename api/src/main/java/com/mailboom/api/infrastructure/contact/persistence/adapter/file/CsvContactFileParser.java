package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.infrastructure.contact.dto.ContactData;
import com.mailboom.api.domain.port.out.ContactFileParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Component
public class CsvContactFileParser implements ContactFileParser {

    private static final String SEPARATOR = ",";
    private static final String DEFAULT_NAME = "User";

    @Override
    public void parse(InputStream inputStream, Consumer<ContactData> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return; // Empty file
            }

            List<String> headers = Arrays.asList(headerLine.toLowerCase().split(SEPARATOR));
            int emailIndex = findColumnIndex(headers, "email");
            if (emailIndex == -1) {
                throw new IllegalArgumentException("CSV file must have an 'email' column");
            }
            
            int nameIndex = findColumnIndex(headers, "name");

            reader.lines().forEach(line -> {
                String[] parts = line.split(SEPARATOR);
                if (parts.length > emailIndex) {
                    String email = parts[emailIndex].trim();
                    if (isValidEmail(email)) {
                        String name = DEFAULT_NAME;
                        
                        if (nameIndex != -1 && parts.length > nameIndex) {
                            String extractedName = parts[nameIndex].trim();
                            if (!extractedName.isEmpty()) {
                                name = extractedName;
                            }
                        }
                        
                        Map<String, Object> customFields = new HashMap<>();
                        consumer.accept(new ContactData(email, name, customFields));
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV file", e);
        }
    }

    private int findColumnIndex(List<String> headers, String columnName) {
        return IntStream.range(0, headers.size())
                .filter(i -> headers.get(i).trim().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(-1);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}
