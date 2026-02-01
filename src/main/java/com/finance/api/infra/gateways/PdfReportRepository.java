package com.finance.api.infra.gateways;

import com.finance.api.application.gateways.ReportGateway;
import com.finance.api.domain.transaction.Transaction;
import com.finance.api.domain.transaction.TransactionType;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfReportRepository implements ReportGateway {

    private final ExcelReportRepository excelReportRepository;

    public PdfReportRepository(ExcelReportRepository excelReportRepository) {
        this.excelReportRepository = excelReportRepository;
    }

    @Override
    public byte[] generateExcel(List<Transaction> transactions) {
        return excelReportRepository.generateExcel(transactions);
    }

    @Override
    public byte[] generatePdf(List<Transaction> transactions, String userName) {
        if (transactions == null) {
            throw new IllegalArgumentException("The list of transactions cannot be null");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 102, 204));
            Paragraph title = new Paragraph("Finance Extract - Monetra", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Paragraph userInfo = new Paragraph("User: " + userName, infoFont);
            userInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(userInfo);

            Paragraph dateInfo = new Paragraph("Report date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), infoFont);
            dateInfo.setAlignment(Element.ALIGN_CENTER);
            dateInfo.setSpacingAfter(20);
            document.add(dateInfo);

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;

            for (Transaction t : transactions) {
                if (t.getFinalAmount() == null) {
                    continue; 
                }
                
                if (t.getType() == TransactionType.RECEITA) {
                    totalIncome = totalIncome.add(t.getFinalAmount());
                } else if (t.getType() == TransactionType.DESPESA) {
                    totalExpense = totalExpense.add(t.getFinalAmount());
                }
            }

            BigDecimal balance = totalIncome.subtract(totalExpense);

            Font summaryTitleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph summaryTitle = new Paragraph("Summary", summaryTitleFont);
            summaryTitle.setSpacingAfter(10);
            document.add(summaryTitle);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            addSummaryRow(summaryTable, "Total Income:", formatCurrency(totalIncome), new Color(0, 128, 0));
            addSummaryRow(summaryTable, "Total Expenses:", formatCurrency(totalExpense), new Color(220, 20, 60));
            addSummaryRow(summaryTable, "Amount:", formatCurrency(balance), balance.compareTo(BigDecimal.ZERO) >= 0 ? new Color(0, 128, 0) : new Color(220, 20, 60));

            summaryTable.setSpacingAfter(20);
            document.add(summaryTable);

            Font tableTitleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph tableTitle = new Paragraph("Transactions", tableTitleFont);
            tableTitle.setSpacingAfter(10);
            document.add(tableTitle);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 4, 2, 2, 2});

            addTableHeader(table, "Date");
            addTableHeader(table, "Description");
            addTableHeader(table, "Type");
            addTableHeader(table, "Status");
            addTableHeader(table, "Amount");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int rowCount = 0;
            for (Transaction t : transactions) {
                try {
                    String dateStr = (t.getDate() != null) ? t.getDate().format(dateFormatter) : "-";
                    String descStr = (t.getDescription() != null) ? t.getDescription() : "No description";
                    String typeStr = (t.getType() != null) ? t.getType().toString() : "UNKNOWN";
                    String statusStr = (t.getStatus() != null) ? t.getStatus().toString() : "UNKNOWN";
                    
                    addTableCell(table, dateStr);
                    addTableCell(table, descStr);
                    addTypeCell(table, t.getType());
                    addTableCell(table, statusStr);
                    addAmountCell(table, t.getFinalAmount(), t.getType());
                    
                    rowCount++;
                } catch (Exception e) {
                    throw new RuntimeException("Error render ", e);
                }
            }

            document.add(table);

            Paragraph footer = new Paragraph("\n\nReport automatically generated by MonetraAPI.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error to generate", e);
        }
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(new Color(0, 102, 204));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 10)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTypeCell(PdfPTable table, TransactionType type) {
        if (type == null) {
             PdfPCell cell = new PdfPCell(new Phrase("-", new Font(Font.HELVETICA, 10)));
             table.addCell(cell);
             return;
        }
        
        Color color = type == TransactionType.RECEITA ? new Color(0, 128, 0) : new Color(220, 20, 60);
        PdfPCell cell = new PdfPCell(new Phrase(type.toString(), new Font(Font.HELVETICA, 10, Font.BOLD, color)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addAmountCell(PdfPTable table, BigDecimal amount, TransactionType type) {
        if (amount == null) amount = BigDecimal.ZERO;
        
        Color color = (type == TransactionType.RECEITA) ? new Color(0, 128, 0) : new Color(220, 20, 60);
        String prefix = (type == TransactionType.RECEITA) ? "+ " : "- ";
        
        PdfPCell cell = new PdfPCell(new Phrase(prefix + formatCurrency(amount), new Font(Font.HELVETICA, 10, Font.BOLD, color)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Color valueColor) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, new Font(Font.HELVETICA, 11, Font.BOLD)));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 11, Font.BOLD, valueColor)));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "R$ 0,00";
        return String.format("R$ %,.2f", value);
    }
}