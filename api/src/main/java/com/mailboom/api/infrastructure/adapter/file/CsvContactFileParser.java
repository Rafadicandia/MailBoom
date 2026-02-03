package com.mailboom.api.infrastructure.adapter.file;

import com.mailboom.api.infrastructure.dto.ContactData;
import com.mailboom.api.domain.port.out.ContactFileParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

@Component
public class CsvContactFileParser implements ContactFileParser {

    @Override
    public void parse(InputStream inputStream, Consumer<ContactData> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header if present, or handle logic to detect header
                    continue; 
                }
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    String email = parts[0].trim();
                    String name = parts.length > 1 ? parts[1].trim() : "";
                    consumer.accept(new ContactData(email, name, new HashMap<>()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV file", e);
        }
    }
}
