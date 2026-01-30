package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.ReportGateway;
import com.finance.api.domain.transaction.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ExcelReportRepository implements ReportGateway {

    @Override
    public byte[] generateExcel(List<Transaction> transactions) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Finance Extract");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Date", "Description", "Type", "Amount"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowIndex++);

                Cell dateCell = row.createCell(0);
                if (t.getDate() != null) {
                    dateCell.setCellValue(t.getDate());
                    dateCell.setCellStyle(dateStyle);
                }

                row.createCell(1).setCellValue(t.getDescription());

                row.createCell(2).setCellValue(t.getType().toString());

                Cell amountCell = row.createCell(3);
                amountCell.setCellValue(t.getFinalAmount().doubleValue());
                amountCell.setCellStyle(currencyStyle);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ", e);
        }
    }
}