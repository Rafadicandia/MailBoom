package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

            List<String> headers = parseCsvLine(headerLine);
            List<String> lowerCaseHeaders = new ArrayList<>();
            for (String header : headers) {
                lowerCaseHeaders.add(header.toLowerCase().trim());
            }

            int emailIndex = findColumnIndex(lowerCaseHeaders, "email");

            int nameIndex = findColumnIndex(lowerCaseHeaders, "name");
            if (nameIndex == -1) {
                nameIndex = findColumnIndex(lowerCaseHeaders, "nombre");
            }
            int phoneIndex = findColumnIndex(lowerCaseHeaders, "phone");
            if (phoneIndex == -1) {
                phoneIndex = findColumnIndex(lowerCaseHeaders, "telefono");
            }

            // At least one of email or name column must be present
            if (emailIndex == -1 && nameIndex == -1) {
                throw new IllegalArgumentException("CSV file must have at least an 'email' or 'name' column");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parts = parseCsvLine(line);
                // Process row if we have email OR name column with data
                boolean hasEmailData = emailIndex != -1 && parts.size() > emailIndex;
                boolean hasNameData = nameIndex != -1 && parts.size() > nameIndex;
                if (hasEmailData || hasNameData) {
                    String email = null;
                    if (hasEmailData) {
                        email = parts.get(emailIndex).trim();
                        // If email is present but invalid (no @), leave it empty
                        if (!email.isEmpty() && !isValidEmail(email)) {
                            email = null;
                        }
                    }

                    String name = DEFAULT_NAME;

                    String phone = null;
                    if (nameIndex != -1 && parts.size() > nameIndex) {
                        String extractedName = parts.get(nameIndex).trim();
                        if (!extractedName.isEmpty()) {
                            name = extractedName;
                        }
                    }
                    if (phoneIndex != -1 && parts.size() > phoneIndex) {
                        phone = parts.get(phoneIndex).trim();
                        if (phone.isEmpty()) {
                            phone = null;
                        }
                    }

                    Map<String, Object> customFields = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        if (i == emailIndex || i == nameIndex || i == phoneIndex) {
                            continue;
                        }
                        if (i < parts.size()) {
                            String value = parts.get(i).trim();
                            if (!value.isEmpty()) {
                                String headerName = headers.get(i).trim();
                                if (!headerName.isEmpty()) {
                                    customFields.put(headerName, value);
                                }
                            }
                        }
                    }
                    consumer.accept(new ContactData(email, name, phone, customFields));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV file", e);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote inside quoted field
                    currentField.append('"');
                    i++;
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == SEPARATOR.charAt(0) && !inQuotes) {
                // Field separator outside quotes
                result.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        result.add(currentField.toString());

        return result;
    }

    private int findColumnIndex(List<String> headers, String columnName) {
        return IntStream.range(0, headers.size())
                .filter(i -> headers.get(i).equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(-1);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }
}
