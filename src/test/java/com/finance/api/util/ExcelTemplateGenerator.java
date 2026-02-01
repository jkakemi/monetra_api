package com.finance.api.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTemplateGenerator {

    public static void main(String[] args) throws IOException {
        generateUsersTemplate();
    }

    public static void generateUsersTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"name", "email", "password", "cpf"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Example data rows
            Object[][] data = {
                {"João Silva", "joao.silva@email.com", "senha123", "123.456.789-00"},
                {"Maria Santos", "maria.santos@email.com", "senha456", "987.654.321-00"},
                {"Pedro Oliveira", "pedro.oliveira@email.com", "senha789", "456.789.123-00"}
            };

            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < data[i].length; j++) {
                    row.createCell(j).setCellValue((String) data[i][j]);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream("docs/users_template.xlsx")) {
                workbook.write(fileOut);
            }

            System.out.println("Template created: docs/users_template.xlsx");
        }
    }
}
