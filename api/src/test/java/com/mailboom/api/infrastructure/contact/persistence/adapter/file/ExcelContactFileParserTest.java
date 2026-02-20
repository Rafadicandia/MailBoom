package com.mailboom.api.infrastructure.contact.persistence.adapter.file;

import com.mailboom.api.infrastructure.contact.dto.ContactData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelContactFileParserTest {

    private final ExcelContactFileParser parser = new ExcelContactFileParser();

    @Test
    void shouldParseExcelFileCorrectly() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");

            // Header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nombre");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Telefono");

            // Data rows
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Juan Perez");
            row1.createCell(1).setCellValue("juan.perez@example.com");
            row1.createCell(2).setCellValue("123456789");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue(""); // Empty name
            row2.createCell(1).setCellValue("maria.garcia@test.com");
            
            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            // Parse
            List<ContactData> contacts = new ArrayList<>();
            parser.parse(inputStream, contacts::add);

            // Assertions
            assertFalse(contacts.isEmpty());
            assertEquals(2, contacts.size());

            ContactData contact1 = contacts.get(0);
            assertEquals("juan.perez@example.com", contact1.email());
            assertEquals("Juan Perez", contact1.name()); // Name from file

            ContactData contact2 = contacts.get(1);
            assertEquals("maria.garcia@test.com", contact2.email());
            assertEquals("User", contact2.name()); // Default name because cell was empty
        }
    }

    @Test
    void shouldAssignDefaultNameWhenNameColumnIsMissing() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Email");
            headerRow.createCell(1).setCellValue("Telefono"); // No name column

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("user@example.com");
            row1.createCell(1).setCellValue("123456");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            List<ContactData> contacts = new ArrayList<>();
            parser.parse(inputStream, contacts::add);

            assertEquals(1, contacts.size());
            assertEquals("user@example.com", contacts.get(0).email());
            assertEquals("User", contacts.get(0).name()); // Default name
        }
    }

    @Test
    void shouldThrowExceptionWhenEmailColumnMissing() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nombre");
            headerRow.createCell(1).setCellValue("Telefono"); // No email column

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

            assertThrows(RuntimeException.class, () -> parser.parse(inputStream, c -> {}));
        }
    }
}
