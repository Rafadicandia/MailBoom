package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.domain.port.out.ContactFileParser;
import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class ExcelContactFileParser implements ContactFileParser {

    private static final String DEFAULT_NAME = "User";

    @Override
    public void parse(InputStream inputStream, Consumer<ContactData> consumer) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return; // Empty file
            }

            // Read header
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            Integer emailIndex = headerMap.get("email");
            if (emailIndex == null) {
                throw new IllegalArgumentException("Excel file must have an 'email' column");
            }
            Integer nameIndex = headerMap.get("name");

            // Read data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell emailCell = row.getCell(emailIndex);
                
                if (emailCell != null) {
                    String email = getCellValueAsString(emailCell).trim();
                    if (isValidEmail(email)) {
                        String name = DEFAULT_NAME;
                        if (nameIndex != null) {
                            Cell nameCell = row.getCell(nameIndex);
                            if (nameCell != null) {
                                String extractedName = getCellValueAsString(nameCell).trim();
                                if (!extractedName.isEmpty()) {
                                    name = extractedName;
                                }
                            }
                        }
                        
                        Map<String, Object> customFields = new HashMap<>();
                        consumer.accept(new ContactData(email, name, customFields));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error parsing Excel file", e);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Avoid scientific notation for phone numbers, etc.
                    double value = cell.getNumericCellValue();
                    if (value == (long) value) {
                        return String.format("%d", (long) value);
                    } else {
                        return String.format("%s", value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && email.contains("@");
    }
}
